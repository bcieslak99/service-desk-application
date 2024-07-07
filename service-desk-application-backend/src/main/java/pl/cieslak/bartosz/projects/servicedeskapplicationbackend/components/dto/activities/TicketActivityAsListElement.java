package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.activities;

import lombok.*;
import org.springframework.stereotype.Component;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.user.UserDetailsDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketActivityType;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class TicketActivityAsListElement
{
    private UUID id;
    private TicketActivityType ticketActivityType;
    private String description;
    private LocalDateTime activityDate;
    private UserDetailsDTO analyst;
}
