package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tasks;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Component;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.task.CommentDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
@Entity
@Table(name = "TASK_COMMENTS")
public class TaskComment
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotEmpty
    @NotBlank
    @Column(name = "COMMENT", nullable = false)
    private String comment;

    @CreationTimestamp
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "AUTHOR", updatable = false, nullable = false)
    private User authorOfComment;

    @ManyToOne
    @JoinColumn(name = "TASK_SET", updatable = false, nullable = false)
    private TaskSet taskSet;

    public CommentDTO prepareCommentToShow()
    {
        return CommentDTO
                .builder()
                .id(this.id)
                .comment(this.comment)
                .createdAt(this.createdAt)
                .author(this.authorOfComment.prepareUserDetails())
                .build();
    }
}
