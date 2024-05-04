package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.categories;

import lombok.*;
import org.springframework.stereotype.Component;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketType;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class CategoryDetailsDTO
{
    private UUID categoryId;
    private String name;
    private String description;
    private boolean categoryActive;
    private TicketType ticketType;
    private UUID defaultGroup;
}
