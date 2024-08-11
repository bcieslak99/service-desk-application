package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tasks;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.stereotype.Component;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.task.TaskDTO;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
@Entity
@Table(name = "TASKS")
public class Task implements Comparable<Task>
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotEmpty
    @NotBlank
    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "DONE", nullable = false)
    private boolean done = false;

    @Column(name = "POSITION", nullable = false)
    private int position;

    @ManyToOne
    @JoinColumn(name = "TASKS_SET")
    private TaskSet container;

    public TaskDTO prepareTaskToShow()
    {
        return TaskDTO
                .builder()
                .id(this.id)
                .description(this.description)
                .done(this.done)
                .position(this.position)
                .build();
    }

    @Override
    public int compareTo(Task otherTask)
    {
        return Integer.compare(this.position, otherTask.getPosition());
    }
}
