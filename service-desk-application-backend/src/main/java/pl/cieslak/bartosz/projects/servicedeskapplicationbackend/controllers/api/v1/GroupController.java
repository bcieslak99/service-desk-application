package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.controllers.api.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.groups.GroupDetailsDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.user.UserId;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.groups.NewGroupDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.responses.ResponseCode;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.responses.ResponseMessage;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.GroupsService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/group")
public class GroupController
{
    private final GroupsService GROUP_SERVICE;

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<List<GroupDetailsDTO>> showGroups()
    {
        return this.GROUP_SERVICE.getAllGroups();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<?> showGroupDetails(@PathVariable("id") UUID groupId)
    {
        return this.GROUP_SERVICE.getGroupDetails(groupId);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<ResponseMessage> createGroup(@Valid @RequestBody NewGroupDTO groupData, BindingResult errors)
    {
        if(errors.hasErrors())
        {
            ResponseMessage responseMessage =
                    new ResponseMessage("Nie udało się utworzyć grupy! Dane są nieprawidłowe!", ResponseCode.ERROR);

            return ResponseEntity.badRequest().body(responseMessage);
        }

        return this.GROUP_SERVICE.createGroup(groupData);
    }

    @PatchMapping("/activate/{id}")
    @PreAuthorize("hasAuthority('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<ResponseMessage> activateGroup(@PathVariable("id") UUID groupId)
    {
        return this.GROUP_SERVICE.activateGroup(groupId);
    }

    @PatchMapping("/deactivate/{id}")
    @PreAuthorize("hasAuthority('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<ResponseMessage> deactivateGroup(@PathVariable("id") UUID groupId)
    {
        return this.GROUP_SERVICE.deactivateGroup(groupId);
    }

    @PatchMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<ResponseMessage> editGroup(@PathVariable("id") UUID groupId,
                                                     @Valid @RequestBody NewGroupDTO groupData, BindingResult errors)
    {
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(new ResponseMessage("Edycja zakończona błędem. Podano nieprawidłowe dane!",
                    ResponseCode.ERROR));

        return this.GROUP_SERVICE.editGroupData(groupId, groupData);
    }

    @PostMapping("/member/add/{id}")
    @PreAuthorize("hasAuthority('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<ResponseMessage> addMemberToGroup(@PathVariable("id") UUID groupId,
                                                            @Valid @RequestBody UserId memberId, BindingResult errors)
    {
        if(groupId == null || errors.hasErrors())
            return ResponseEntity.badRequest().body(new ResponseMessage("Błąd! Podano nie prawidłowe dane!", ResponseCode.ERROR));

        return this.GROUP_SERVICE.addMemberToGroup(groupId, memberId.getUserId());
    }

    @DeleteMapping("/member/remove/{id}")
    @PreAuthorize("hasAuthority('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<ResponseMessage> removeMemberFromGroup(@PathVariable("id") UUID groupId,
                                                                 @Valid @RequestBody UserId memberId, BindingResult errors)
    {
        if(groupId == null || errors.hasErrors())
            return ResponseEntity.badRequest().body(new ResponseMessage("Błąd! Podano nie prawidłowe dane!", ResponseCode.ERROR));

        return this.GROUP_SERVICE.removeMemberToGroup(groupId, memberId.getUserId());
    }

    @GetMapping("/members/{id}")
    @PreAuthorize("hasAuthority('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<?> getMembersOfGroup(@PathVariable("id") UUID groupId)
    {
        return this.GROUP_SERVICE.getGroupMembers(groupId);
    }

    @GetMapping("/members/management/{id}")
    @PreAuthorize("hasAuthority('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<?> getMembersToModify(@PathVariable("id") UUID groupId)
    {
        return this.GROUP_SERVICE.getMembersToModify(groupId);
    }

    @PatchMapping("/manager/set/{id}")
    @PreAuthorize("hasAuthority('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<ResponseMessage> setManager(@PathVariable("id") UUID groupId, @RequestBody UserId managerId)
    {
        return this.GROUP_SERVICE.changeGroupManager(groupId, managerId.getUserId());
    }
}
