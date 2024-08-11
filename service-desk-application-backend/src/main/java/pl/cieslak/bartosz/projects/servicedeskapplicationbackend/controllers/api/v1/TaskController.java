package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.controllers.api.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.responses.ResponseCode;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.responses.ResponseMessage;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.task.CommentFormDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.task.TaskSetFormDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.system.PermissionDeniedException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.task.IncorrectTaskSetException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.task.TaskSetNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.users.UserNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.TaskService;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.jwt.AuthService;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/task")
public class TaskController
{

    private static final String INTERNAL_SERVER_ERROR = "Napotkano na nieoczekiwany błąd!";
    private final TaskService TASK_SERVICE;
    private final AuthService AUTH_SERVICE;

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('FIRST_LINE_ANALYST', 'SECOND_LINE_ANALYST')")
    public ResponseEntity<ResponseMessage> createTask(Principal principal, @Valid @RequestBody TaskSetFormDTO taskSetForm, BindingResult errors)
    {
        if(!this.AUTH_SERVICE.userIsGroupManager(principal))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Nie masz uprawnień do tworzenia zadań!", ResponseCode.ERROR));

        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(new ResponseMessage("Zbiór zadań zawiera błędy!", ResponseCode.ERROR));

        try
        {
            this.TASK_SERVICE.createTaskSet(taskSetForm, principal);
            return ResponseEntity.ok(new ResponseMessage("Zadanie zostało utworzone", ResponseCode.SUCCESS));
        }
        catch(IncorrectTaskSetException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage(INTERNAL_SERVER_ERROR, ResponseCode.ERROR));
        }
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('FIRST_LINE_ANALYST', 'SECOND_LINE_ANALYST')")
    public ResponseEntity<?> getTasks(Principal principal)
    {
        try
        {
            return ResponseEntity.ok(this.TASK_SERVICE.getTasks(principal));
        }
        catch(UserNotFoundException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
            return ResponseEntity.internalServerError().body(new ResponseMessage(INTERNAL_SERVER_ERROR, ResponseCode.ERROR));
        }
    }

    @GetMapping("/{taskSetId}")
    @PreAuthorize("hasAnyAuthority('FIRST_LINE_ANALYST', 'SECOND_LINE_ANALYST')")
    public ResponseEntity<?> getTask(@PathVariable("taskSetId") UUID taskSetId, Principal principal)
    {
        try
        {
            return ResponseEntity.ok(this.TASK_SERVICE.getTaskSet(taskSetId, principal));
        }
        catch(UserNotFoundException | TaskSetNotFoundException | PermissionDeniedException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch (Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage(INTERNAL_SERVER_ERROR, ResponseCode.ERROR));
        }
    }

    @PatchMapping("/done/{taskId}")
    @PreAuthorize("hasAnyAuthority('FIRST_LINE_ANALYST', 'SECOND_LINE_ANALYST')")
    public ResponseEntity<ResponseMessage> setTaskAsDone(@PathVariable("taskId") UUID taskId, Principal principal)
    {
        try
        {
            this.TASK_SERVICE.setTaskAsDone(taskId, principal);
            return ResponseEntity.ok(new ResponseMessage("Zadanie zostało oznaczone jako zrealizowane!", ResponseCode.SUCCESS));
        }
        catch (UserNotFoundException | PermissionDeniedException | TaskSetNotFoundException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage(INTERNAL_SERVER_ERROR, ResponseCode.ERROR));
        }
    }

    @PostMapping("/comment/{taskId}")
    @PreAuthorize("hasAnyAuthority('FIRST_LINE_ANALYST', 'SECOND_LINE_ANALYST')")
    public ResponseEntity<ResponseMessage> addComment(@PathVariable("taskId") UUID taskId, Principal principal, @Valid @RequestBody CommentFormDTO comment, BindingResult errors)
    {
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(new ResponseMessage("Komentarz zawiera błędy!", ResponseCode.ERROR));

        try
        {
            this.TASK_SERVICE.addComment(taskId, principal, comment.getComment());
            return ResponseEntity.ok(new ResponseMessage("Komentarz został dodany.", ResponseCode.SUCCESS));
        }
        catch (UserNotFoundException | PermissionDeniedException | TaskSetNotFoundException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage(INTERNAL_SERVER_ERROR, ResponseCode.ERROR));
        }
    }
}
