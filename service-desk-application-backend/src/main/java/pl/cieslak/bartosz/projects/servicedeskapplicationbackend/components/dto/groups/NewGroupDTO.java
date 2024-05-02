package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.groups;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.stereotype.Component;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.groups.GroupType;

import java.util.UUID;

import static pl.cieslak.bartosz.projects.servicedeskapplicationbackend.configuration.data.validation.GroupDataValidation.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class NewGroupDTO
{
    @NotNull
    @NotBlank
    @Size(min = MINIMUM_LENGTH_OF_GROUP_NAME, max = MAXIMUM_LENGTH_OF_GROUP_NAME)
    private String name;

    @NotNull
    @NotBlank
    @Size(min = MINIMUM_LENGTH_OF_GROUP_DESCRIPTION, max = MAXIMUM_LENGTH_OF_GROUP_DESCRIPTION)
    private String description;

    @NotNull
    private GroupType groupType;

    private boolean groupActive;

    private UUID managerId;
}
