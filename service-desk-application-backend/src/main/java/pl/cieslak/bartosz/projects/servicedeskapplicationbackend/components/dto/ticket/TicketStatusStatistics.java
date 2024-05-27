package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.ticket;

import lombok.*;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class TicketStatusStatistics
{
    private long pending;
    private long inProgress;
    private long onHold;
    private long resolved;
}
