package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.ticket;

import lombok.*;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class TicketTypeStatisticsDTO
{
    private TicketStatusStatistics incidents = new TicketStatusStatistics();
    private TicketStatusStatistics serviceRequests = new TicketStatusStatistics();
}
