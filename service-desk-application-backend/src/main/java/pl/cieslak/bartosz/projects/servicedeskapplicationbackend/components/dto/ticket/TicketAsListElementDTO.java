package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.ticket;

import lombok.*;
import org.springframework.stereotype.Component;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.user.UserDetailsDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketStatus;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketType;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class TicketAsListElementDTO
{
    private UUID id;
    private TicketType ticketType;
    private TicketStatus ticketStatus;
    private String description;
    private UserDetailsDTO customer;
    private UserDetailsDTO reporter;

}
