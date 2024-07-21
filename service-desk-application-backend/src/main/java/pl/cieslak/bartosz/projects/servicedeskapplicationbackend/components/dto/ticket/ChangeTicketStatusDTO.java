package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.ticket;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.stereotype.Component;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketStatus;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class ChangeTicketStatusDTO
{
    @NotNull
    private TicketStatus newTicketStatus;
    private String comment;
}
