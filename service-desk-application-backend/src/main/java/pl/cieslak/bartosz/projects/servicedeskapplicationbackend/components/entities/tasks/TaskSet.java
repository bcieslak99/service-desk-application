package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tasks;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.stereotype.Component;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.task.CommentDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.task.TaskDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.task.TaskSetAsListElementDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.task.TaskSetDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.groups.SupportGroup;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
@Entity
@Table(name = "TASK_SETS")
public class TaskSet
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "TITLE", nullable = false)
    private String title;

    @UpdateTimestamp
    @Column(name = "LAST_ACTIVITY", nullable = false)
    private LocalDateTime lastActivity = LocalDateTime.now();

    @Column(name = "PLANNED_END_DATE", nullable = false, updatable = false)
    private LocalDateTime plannedEndDate;

    @Column(name = "REAL_END_DATE")
    private LocalDateTime realEndDate;

    @Column(name = "LAST_NOTIFICATION")
    private LocalDateTime lastNotification;

    @OneToMany(mappedBy = "container")
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "taskSet")
    private List<TaskComment> comments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "SUPPORT_GROUP", nullable = false, updatable = false)
    private SupportGroup group;

    @PrePersist
    private void updateDateOfLastActivity()
    {
        this.lastActivity = LocalDateTime.now();
    }

    public TaskSetDTO prepareTaskSetToShow()
    {
        TaskSetDTO taskSet = TaskSetDTO
                .builder()
                .id(this.id)
                .title(this.title)
                .plannedEndDate(this.plannedEndDate)
                .group(this.group.getName())
                .build();

        ArrayList<TaskDTO> tasks = new ArrayList<>();
        if(this.tasks.size() > 0) tasks.ensureCapacity(this.tasks.size());
        this.tasks.forEach(task -> tasks.add(task.prepareTaskToShow()));
        Collections.sort(tasks);
        taskSet.setTasks(tasks);

        ArrayList<CommentDTO> comments = new ArrayList<>();
        if(this.comments.size() > 0) comments.ensureCapacity(this.comments.size());
        this.comments.forEach(comment -> comments.add(comment.prepareCommentToShow()));
        Collections.sort(comments);
        taskSet.setComments(comments);

        return taskSet;
    }

    public TaskSetAsListElementDTO prepareTaskSetAsListElement()
    {
        return TaskSetAsListElementDTO
                .builder()
                .id(this.id)
                .title(this.title)
                .group(this.group.getName())
                .plannedEndDate(this.plannedEndDate)
                .build();
    }
}
