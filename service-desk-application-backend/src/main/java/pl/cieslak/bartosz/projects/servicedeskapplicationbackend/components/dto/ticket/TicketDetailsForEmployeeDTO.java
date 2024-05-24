package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.ticket;

import lombok.*;
import org.springframework.stereotype.Component;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.categories.CategoryDetailsForEmployeeDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.user.UserDetailsDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketStatus;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketType;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class TicketDetailsForEmployeeDTO
{
    private UUID id;
    private TicketType ticketType;
    private String description;
    private UserDetailsDTO customer;
    private UserDetailsDTO reporter;
    private TicketStatus status;
    private LocalDateTime openDate;
    private LocalDateTime resolveDate;
    private LocalDateTime closeDate;
    private CategoryDetailsForEmployeeDTO category;
}
