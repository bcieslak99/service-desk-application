package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.categories;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.stereotype.Component;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketType;

import java.util.UUID;

import static pl.cieslak.bartosz.projects.servicedeskapplicationbackend.configuration.data.validation.TicketCategoryDataValidation.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class NewTicketCategoryDTO
{
    @NotNull
    @NotBlank
    @Size(min = MINIMUM_LENGTH_OF_CATEGORY_NAME, max = MAXIMUM_LENGTH_OF_CATEGORY_NAME)
    private String name;

    @NotNull
    @NotBlank
    @Size(min = MINIMUM_LENGTH_OF_CATEGORY_DESCRIPTION, max = MAXIMUM_LENGTH_OF_CATEGORY_DESCRIPTION)
    private String description;

    @NotNull
    private TicketType ticketType;

    @NotNull
    private UUID defaultGroup;
}
