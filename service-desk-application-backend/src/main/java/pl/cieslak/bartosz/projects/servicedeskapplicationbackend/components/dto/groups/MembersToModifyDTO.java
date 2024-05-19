package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.groups;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class MembersToModifyDTO
{
    private List<GroupMemberDetailsDTO> addedUsers = new ArrayList<>();
    private List<GroupMemberDetailsDTO> otherUsers = new ArrayList<>();
}
