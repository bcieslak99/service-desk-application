package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.task;

import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class TaskSetDTO
{
    private UUID id;
    private String title;
    private String group;
    private LocalDateTime plannedEndDate;
    List<TaskDTO> tasks = new ArrayList<>();
    List<CommentDTO> comments = new ArrayList<>();
}
