package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.task;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class TaskDTO implements Comparable<TaskDTO>
{
    private UUID id;
    private String description;
    private int position;
    private boolean done;

    @Override
    public int compareTo(TaskDTO otherTask)
    {
        return Integer.compare(this.position, otherTask.getPosition());
    }
}
