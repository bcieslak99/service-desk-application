package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.groups.GroupDetailsDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.groups.NewGroupDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.responses.ResponseCode;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.responses.ResponseMessage;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.groups.SupportGroup;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.groups.GroupCreationException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.groups.GroupNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.groups.GroupExistsException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.groups.GroupIncorrectDataException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.users.UserNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.group.GroupsRepository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.user.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupsService
{
    private final GroupsRepository GROUP_REPOSITORY;
    private final UserService USER_SERVICE;

    public GroupDetailsDTO prepareGroupAsListElement(SupportGroup group)
    {
        if(group == null) return null;

        return GroupDetailsDTO.builder()
                .groupId(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .groupType(group.getGroupType())
                .groupActive(group.isGroupActive())
                .manager(this.USER_SERVICE.prepareUserAsContact(group.getGroupManager()))
                .build();
    }

    public SupportGroup prepareSupportGroup(NewGroupDTO groupData)
    {
        if(groupData == null) return null;

        SupportGroup group = new SupportGroup();
        group.setName(groupData.getName());
        group.setDescription(group.getDescription());
        group.setGroupType(group.getGroupType());
        group.setGroupActive(true);
        group.setGroupManager(this.USER_SERVICE.getUserById(groupData.getManagerId()).orElse(null));

        return group;
    }

    public ResponseEntity<List<GroupDetailsDTO>> getActiveGroups()
    {
        List<SupportGroup> groups = this.GROUP_REPOSITORY.getActiveGroups();
        List<GroupDetailsDTO> groupsToShow = new ArrayList<>();

        for(SupportGroup element : groups)
            groupsToShow.add(prepareGroupAsListElement(element));

        groupsToShow = groupsToShow.stream().filter(Objects::nonNull).collect(Collectors.toList());

        return ResponseEntity.ok(groupsToShow);
    }

    public Optional<SupportGroup> getGroupAndMembersById(UUID groupId)
    {
        if(groupId == null) return Optional.empty();
        return this.GROUP_REPOSITORY.getSupportGroupAndMembersById(groupId);
    }

    public boolean groupExists(String name)
    {
        if(name == null || name.trim().isEmpty()) return false;
        return this.GROUP_REPOSITORY.getSupportGroupByName(name.trim()).isPresent();
    }

    public ResponseEntity<ResponseMessage> createGroup(NewGroupDTO groupData)
    {
        try
        {
            if(groupExists(groupData.getName()))
                throw new GroupExistsException("Grupa z taką nazwą już istnieje!");

            SupportGroup group = prepareSupportGroup(groupData);
            if(group == null) throw new GroupCreationException("Nie udało się utworzyć grupy!");

            if(this.GROUP_REPOSITORY.saveAndFlush(group) == null)
                throw new GroupCreationException("Nie udało się utworzyć grupy!");
            else return ResponseEntity.ok(new ResponseMessage("Grupa została utworzona!", ResponseCode.SUCCESS));
        }
        catch(GroupExistsException | GroupCreationException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError()
                    .body(new ResponseMessage("Napotkano nieoczekiwane błąd podczas tworzenia grupy!",
                            ResponseCode.ERROR));
        }
    }

    public ResponseEntity<ResponseMessage> activateGroup(UUID groupId)
    {
        final String ACTIVATED_GROUP_DOES_NOT_EXISTS_MESSAGE = "Błąd podczas aktywacji! Nie odnaleziono takiej grupy!";

        try
        {
            if(groupId == null) throw new GroupNotFoundException(ACTIVATED_GROUP_DOES_NOT_EXISTS_MESSAGE);

            Optional<SupportGroup> groupInDatabase = this.GROUP_REPOSITORY.findById(groupId);
            if(groupInDatabase.isEmpty()) throw new GroupNotFoundException(ACTIVATED_GROUP_DOES_NOT_EXISTS_MESSAGE);

            SupportGroup group = groupInDatabase.get();

            if(group.isGroupActive())
                return ResponseEntity.ok(new ResponseMessage("Ta grupa była już aktywna.", ResponseCode.SUCCESS));
            else
            {
                group.setGroupActive(true);
                this.GROUP_REPOSITORY.saveAndFlush(group);

                return ResponseEntity.ok(new ResponseMessage("Grupa została aktywowana.", ResponseCode.SUCCESS));
            }
        }
        catch(GroupNotFoundException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError()
                    .body(new ResponseMessage("Napotkano na nieoczekiwany błąd podczas aktywacji grupy!",
                            ResponseCode.ERROR));
        }
    }

    public ResponseEntity<ResponseMessage> deactivateGroup(UUID groupId)
    {
        final String ACTIVATED_GROUP_DOES_NOT_EXISTS_MESSAGE = "Błąd podczas dezaktywacji! Nie odnaleziono takiej grupy!";

        try
        {
            if(groupId == null) throw new GroupNotFoundException(ACTIVATED_GROUP_DOES_NOT_EXISTS_MESSAGE);

            Optional<SupportGroup> groupInDatabase = this.GROUP_REPOSITORY.findById(groupId);
            if(groupInDatabase.isEmpty()) throw new GroupNotFoundException(ACTIVATED_GROUP_DOES_NOT_EXISTS_MESSAGE);

            SupportGroup group = groupInDatabase.get();

            if(!group.isGroupActive())
                return ResponseEntity.ok(new ResponseMessage("Ta grupa była już dezaktywowana.", ResponseCode.SUCCESS));
            else
            {
                group.setGroupActive(false);
                this.GROUP_REPOSITORY.saveAndFlush(group);

                return ResponseEntity.ok(new ResponseMessage("Grupa została aktywowana.", ResponseCode.SUCCESS));
            }
        }
        catch(GroupNotFoundException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError()
                    .body(new ResponseMessage("Napotkano na nieoczekiwany błąd podczas dezaktywacji grupy!",
                            ResponseCode.ERROR));
        }
    }

    public ResponseEntity<ResponseMessage> editGroupData(UUID groupId, NewGroupDTO groupData)
    {
        final String GROUP_DOES_NOT_EXIST = "Błąd edycji grupy! Wskazana grupa nie zostałą odnaleziona!";

        try
        {
            if(groupId == null) throw new GroupNotFoundException(GROUP_DOES_NOT_EXIST);
            if(groupData == null) throw new GroupIncorrectDataException("Nowe dane grupy nie mogą być puste!");

            Optional<SupportGroup> groupInDatabase = this.GROUP_REPOSITORY.findById(groupId);
            if(groupInDatabase.isEmpty()) throw new GroupNotFoundException(GROUP_DOES_NOT_EXIST);

            SupportGroup group = groupInDatabase.get();

            group.setName(group.getName());
            group.setDescription(group.getDescription());
            group.setGroupType(group.getGroupType());
            group.setGroupActive(groupData.isGroupActive());

            if(groupData.getManagerId() == null) group.setGroupManager(null);
            else
            {
                Optional<User> managerInDatabase = this.USER_SERVICE.getUserById(groupData.getManagerId());
                if(managerInDatabase.isEmpty())
                    throw new UserNotFoundException("Błąd podczas edycji grupy! Nie odnaleziono wskazanego kierownika grupy!");
                else group.setGroupManager(managerInDatabase.get());
            }

            this.GROUP_REPOSITORY.saveAndFlush(group);

            return ResponseEntity.ok(new ResponseMessage("Dane grupy zostały zaktualizowane.", ResponseCode.SUCCESS));
        }
        catch(GroupNotFoundException | GroupIncorrectDataException | UserNotFoundException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError()
                    .body(new ResponseMessage("Napotkano na nieoczekiwany błąd podczas edycji grupy!",
                            ResponseCode.ERROR));
        }
    }

    public ResponseEntity<ResponseMessage> addMemberToGroup(UUID groupId, UUID memberId)
    {
        final String GROUP_NOT_FOUND_MESSAGE = "Nie udało się dodać użytkownika do grupy! Nie odnaleziono wskazanej grupy!";
        final String USER_NOT_FOUND_MESSAGE = "Nie udało się dodać użytkownika do grupy! Nie odnaleziono wskazanego użytkownika!";

        try
        {
            if(groupId == null) throw new GroupNotFoundException(GROUP_NOT_FOUND_MESSAGE);
            if(memberId == null) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

            Optional<SupportGroup> groupInDatabase = getGroupAndMembersById(groupId);
            if(groupInDatabase.isEmpty()) throw new GroupNotFoundException(GROUP_NOT_FOUND_MESSAGE);

            Optional<User> userInDatabase = this.USER_SERVICE.getUserById(memberId);
            if(userInDatabase.isEmpty()) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

            SupportGroup group = groupInDatabase.get();
            User user = userInDatabase.get();

            if(group.getGroupMembers().stream().anyMatch(element -> element.getId().equals(user.getId())))
                return ResponseEntity.ok(new ResponseMessage("Ten użytkownik był już dodany do grupy.", ResponseCode.SUCCESS));
            else
            {
                group.getGroupMembers().add(user);
                this.GROUP_REPOSITORY.saveAndFlush(group);
            }

            return ResponseEntity.ok(new ResponseMessage("Użytkownik został dodany do grupy.", ResponseCode.SUCCESS));
        }
        catch(UserNotFoundException | GroupNotFoundException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError()
                    .body(new ResponseMessage("Napotkano nieoczekiwany błąd podczas dodawania użytkownika do grupy!",
                            ResponseCode.ERROR));
        }
    }

    public ResponseEntity<ResponseMessage> removeMemberToGroup(UUID groupId, UUID memberId)
    {
        final String GROUP_NOT_FOUND_MESSAGE = "Nie udało się usunąć użytkownika z grupy! Nie odnaleziono wskazanej grupy!";
        final String USER_NOT_FOUND_MESSAGE = "Nie udało się usunąć użytkownika z grupy! Nie odnaleziono wskazanego użytkownika!";

        try
        {
            if(groupId == null) throw new GroupNotFoundException(GROUP_NOT_FOUND_MESSAGE);
            if(memberId == null) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

            Optional<SupportGroup> groupInDatabase = getGroupAndMembersById(groupId);
            if(groupInDatabase.isEmpty()) throw new GroupNotFoundException(GROUP_NOT_FOUND_MESSAGE);

            SupportGroup group = groupInDatabase.get();
            Optional<User> user = group.getGroupMembers().stream().filter(member -> member.getId().equals(memberId)).findFirst();

            if(user.isPresent()) group.getGroupMembers().remove(user.get());
            else return ResponseEntity.ok(new ResponseMessage("Ten użytkownik nie był członkiem grupy.", ResponseCode.SUCCESS));

            return ResponseEntity.ok(new ResponseMessage("Użytkownik został usunięty z grupy.", ResponseCode.SUCCESS));
        }
        catch(UserNotFoundException | GroupNotFoundException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError()
                    .body(new ResponseMessage("Napotkano nieoczekiwany błąd podczas usuwania użytkownika z grupy!",
                            ResponseCode.ERROR));
        }
    }
}
