package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class TaskFormDTO implements Comparable<TaskFormDTO>
{
    @NotNull
    @NotBlank
    @NotEmpty
    private String description;

    private int position;

    @Override
    public int compareTo(TaskFormDTO otherTask)
    {
        return Integer.compare(this.position, otherTask.getPosition());
    }
}
