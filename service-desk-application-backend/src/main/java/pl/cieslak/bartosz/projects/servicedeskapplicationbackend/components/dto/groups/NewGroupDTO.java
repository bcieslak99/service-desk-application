package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.groups;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.stereotype.Component;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.groups.GroupType;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class NewGroupDTO
{
    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private GroupType groupType;

    private boolean groupActive;

    private UUID managerId;
}
