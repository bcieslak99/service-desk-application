package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static pl.cieslak.bartosz.projects.servicedeskapplicationbackend.configuration.data.validation.TicketDataValidation.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class EmployeeTicketFormDTO
{
    @NotNull
    @NotBlank
    @Size(min = MINIMUM_LENGTH_OF_DESCRIPTION, max = MAXIMUM_LENGTH_OF_DESCRIPTION)
    private String description;

    @NotNull
    private UUID customer;

    @NotNull
    private UUID category;
}
