package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.task;

import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class TaskSetAsListElementDTO implements Comparable<TaskSetAsListElementDTO>
{
    private UUID id;
    private String title;
    private String group;
    private LocalDateTime plannedEndDate;

    @Override
    public int compareTo(TaskSetAsListElementDTO otherTaskSet)
    {
        return this.plannedEndDate.compareTo(otherTaskSet.getPlannedEndDate());
    }
}
