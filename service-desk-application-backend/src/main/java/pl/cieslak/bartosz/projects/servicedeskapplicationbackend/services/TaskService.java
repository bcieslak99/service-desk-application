package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.task.TaskFormDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.task.TaskSetAsListElementDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.task.TaskSetDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.task.TaskSetFormDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.groups.SupportGroup;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tasks.Task;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tasks.TaskComment;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tasks.TaskSet;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.groups.GroupNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.system.PermissionDeniedException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.task.EmptyTaskSetException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.task.IncorrectTaskSetException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.task.TaskSetNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.users.UserNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.task.TaskCommentRepository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.task.TaskRepository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.task.TaskSetRepository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.user.UserService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService
{
    private static final String INCORRECT_TASK_SET_MESSAGE = "Przekazany przez Ciebie zestaw zadań jest nieprawidłowy!";
    private static final String EMPTY_TASK_SET_MESSAGE = "Zestaw zadań nie może być pusty!";
    private static final String MANAGER_PERMISSION_DENIED_MESSAGE = "Nie jesteś kierownikiem grupy! Nie możesz utworzyć zadania!";
    private static final String GROUP_NOT_FOUND_MESSAGE = "Wskazana grupa nie została odnaleziona w systemie!";
    private static final String USER_NOT_FOUND_MESSAGE = "Twoje konto nie zostało rozpoznane.";
    private static final String TASK_SET_NOT_FOUND_MESSAGE = "Zbiór zadań nie został odnaleziony!";

    private final TaskRepository TASK_REPOSITORY;
    private final TaskSetRepository TASK_SET_REPOSITORY;
    private final TaskCommentRepository TASK_COMMENT_REPOSITORY;
    private final GroupsService GROUP_SERVICE;
    private final UserService USER_SERVICE;
    private final MailService MAIL_SERVICE;

    public boolean userCanCreateTaskForGroup(UUID userId, UUID groupId)
    {
        if(userId == null || groupId == null) return false;

        Optional<User> userInDatabase = this.USER_SERVICE.getUserById(userId);
        if(userInDatabase.isEmpty()) return false;

        Optional<SupportGroup> groupInDatabase = this.GROUP_SERVICE.getGroupAndMembersById(groupId);
        if(groupInDatabase.isEmpty()) return false;

        SupportGroup group = groupInDatabase.get();

        return group.getGroupManager() != null &&  group.getGroupManager().getId().equals(userId);
    }

    @Transactional
    public void createTaskSet(TaskSetFormDTO taskSetForm, Principal principal) throws Exception
    {
        if(taskSetForm == null) throw new IncorrectTaskSetException(INCORRECT_TASK_SET_MESSAGE);

        List<TaskFormDTO> tasksFromForm = taskSetForm.getTasks();
        if(tasksFromForm.isEmpty()) throw new EmptyTaskSetException(EMPTY_TASK_SET_MESSAGE);

        if(!userCanCreateTaskForGroup(this.USER_SERVICE.extractUserId(principal), taskSetForm.getSupportGroupId()))
            throw new PermissionDeniedException(MANAGER_PERMISSION_DENIED_MESSAGE);

        Optional<SupportGroup> groupInDatabase = this.GROUP_SERVICE.getGroupById(taskSetForm.getSupportGroupId());
        if(groupInDatabase.isEmpty()) throw new GroupNotFoundException(GROUP_NOT_FOUND_MESSAGE);
        SupportGroup group = groupInDatabase.get();

        TaskSet taskSet = new TaskSet();
        taskSet.setTitle(taskSetForm.getTitle());
        taskSet.setPlannedEndDate(taskSetForm.getPlannedEndDate());
        taskSet.setGroup(group);

        this.TASK_SET_REPOSITORY.saveAndFlush(taskSet);

        ArrayList<Task> tasks = new ArrayList<>();
        if(tasksFromForm.size() > 0) tasks.ensureCapacity(tasksFromForm.size());
        Collections.sort(tasksFromForm);

        for(int i = 0; i < tasksFromForm.size(); i++)
        {
            TaskFormDTO taskInFrom = tasksFromForm.get(i);
            Task task = new Task();
            task.setDescription(taskInFrom.getDescription().trim());
            task.setPosition(i);
            task.setContainer(taskSet);
            tasks.add(task);

            this.TASK_REPOSITORY.saveAndFlush(task);
        }
    }

    public List<TaskSetAsListElementDTO> getTasks(Principal principal) throws Exception
    {
        if(principal == null || principal.getName() == null || principal.getName().isEmpty())
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        List<TaskSet> tasksInDatabase = this.TASK_SET_REPOSITORY.getUserTasks(this.USER_SERVICE.extractUserId(principal));
        tasksInDatabase = tasksInDatabase.stream().filter(element -> element.getRealEndDate() == null || element.getRealEndDate().plusDays(7).isAfter(LocalDateTime.now())).collect(Collectors.toList());

        ArrayList<TaskSetAsListElementDTO> tasks = new ArrayList<>();
        if(tasksInDatabase.size() > 0) tasks.ensureCapacity(tasksInDatabase.size());

        tasksInDatabase.forEach(task -> tasks.add(task.prepareTaskSetAsListElement()));
        Collections.sort(tasks);

        return tasks;
    }

    public TaskSetDTO getTaskSet(UUID taskSetId, Principal principal) throws Exception
    {
        if(taskSetId == null) throw new TaskSetNotFoundException(TASK_SET_NOT_FOUND_MESSAGE);
        if(principal == null || principal.getName() == null || principal.getName().trim().isEmpty())
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        Optional<TaskSet> taskSetInDatabase = this.TASK_SET_REPOSITORY.getTask(taskSetId);
        if(taskSetInDatabase.isEmpty()) throw new TaskSetNotFoundException(TASK_SET_NOT_FOUND_MESSAGE);

        Optional<User> userInDatabase = this.USER_SERVICE.getUserById(this.USER_SERVICE.extractUserId(principal));
        if(userInDatabase.isEmpty()) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        TaskSet taskSet = taskSetInDatabase.get();
        User user = userInDatabase.get();

        if(taskSet.getGroup().getGroupMembers().stream().noneMatch(element -> element.getId().equals(user.getId())))
            throw new PermissionDeniedException("Nie masz uprawnień do tego zbioru zadań!");

        return taskSet.prepareTaskSetToShow();
    }

    public void setTaskAsDone(UUID taskId, Principal principal) throws Exception
    {
        if(taskId == null) throw new TaskSetNotFoundException(TASK_SET_NOT_FOUND_MESSAGE);
        if(principal == null || principal.getName() == null || principal.getName().trim().isEmpty())
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        Optional<Task> taskInDatabase = this.TASK_REPOSITORY.getTaskById(taskId);
        if(taskInDatabase.isEmpty()) throw new TaskSetNotFoundException(TASK_SET_NOT_FOUND_MESSAGE);

        Optional<User> userInDatabase = this.USER_SERVICE.getUserById(this.USER_SERVICE.extractUserId(principal));
        if(userInDatabase.isEmpty()) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        Task task = taskInDatabase.get();
        User user = userInDatabase.get();

        if(task.getContainer().getGroup().getGroupMembers().stream().noneMatch(element -> element.getId().equals(user.getId())))
            throw new PermissionDeniedException("Nie masz uprawnień do edycji tego zadania!");

        Optional<TaskSet> taskSetInDatabase = this.TASK_SET_REPOSITORY.getTask(task.getContainer().getId());
        if(taskSetInDatabase.isEmpty()) throw new TaskSetNotFoundException(TASK_SET_NOT_FOUND_MESSAGE);

        TaskSet taskSet = taskSetInDatabase.get();

        task.setDone(true);
        this.TASK_REPOSITORY.saveAndFlush(task);


        if(taskSet.getTasks().stream().allMatch(Task::isDone))
        {
            taskSet.setRealEndDate(LocalDateTime.now());
            this.TASK_SET_REPOSITORY.saveAndFlush(taskSet);
        }
    }

    public void addComment(UUID taskSetId, Principal principal, String comment) throws Exception
    {
        if(taskSetId == null) throw new TaskSetNotFoundException(TASK_SET_NOT_FOUND_MESSAGE);
        if(principal == null || principal.getName() == null || principal.getName().trim().isEmpty())
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        Optional<TaskSet> taskSetInDatabase = this.TASK_SET_REPOSITORY.getTask(taskSetId);
        if(taskSetInDatabase.isEmpty()) throw new TaskSetNotFoundException(TASK_SET_NOT_FOUND_MESSAGE);

        Optional<User> userInDatabase = this.USER_SERVICE.getUserById(this.USER_SERVICE.extractUserId(principal));
        if(userInDatabase.isEmpty()) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        TaskSet taskSet = taskSetInDatabase.get();
        User user = userInDatabase.get();

        if(taskSet.getGroup().getGroupMembers().stream().noneMatch(element -> element.getId().equals(user.getId())))
            throw new PermissionDeniedException("Nie masz uprawnień do tego zbioru zadań!");

        TaskComment taskComment = new TaskComment();
        taskComment.setComment(comment);
        taskComment.setTaskSet(taskSet);
        taskComment.setAuthorOfComment(user);

        this.TASK_COMMENT_REPOSITORY.saveAndFlush(taskComment);
    }

    @Scheduled(fixedDelay = 1800000)
    public void remindAboutTask()
    {
        List<TaskSet> taskSets = this.TASK_SET_REPOSITORY.getTaskSetsToSendRemind(LocalDateTime.now().plusDays(7), LocalDateTime.now().minusDays(1));

        taskSets.forEach(taskSet ->
        {
            String taskSetTitle = "Tytuł zbioru zadań: " + taskSet.getTitle();
            String supportGroupName = "Grupa wsparcia: " + taskSet.getGroup().getName();
            String plannedEndDate = "Planowana data realizacji: " + DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").format(taskSet.getPlannedEndDate());

            Optional<SupportGroup> groupInDatabase = this.GROUP_SERVICE.getGroupAndMembersById(taskSet.getGroup().getId());

            if(groupInDatabase.isPresent())
            {
                SupportGroup group = groupInDatabase.get();
                List<User> members = group.getGroupMembers();

                members.forEach(member ->
                {
                    try
                    {
                        this.MAIL_SERVICE.sendReminderAboutTask(member.getMail(), supportGroupName, taskSetTitle, plannedEndDate);
                        taskSet.setLastNotification(LocalDateTime.now());
                        this.TASK_SET_REPOSITORY.saveAndFlush(taskSet);
                    }
                    catch (Exception ignored) {}
                });
            }
        });
    }
}
