package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.task;

import lombok.*;
import org.springframework.stereotype.Component;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.user.UserDetailsDTO;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class CommentDTO implements Comparable<CommentDTO>
{
    private UUID id;
    private String comment;
    private LocalDateTime createdAt;
    private UserDetailsDTO author;

    @Override
    public int compareTo(CommentDTO otherComment)
    {
        return otherComment.createdAt.compareTo(this.createdAt);
    }
}
