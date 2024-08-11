package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class TaskSetFormDTO
{
    @NotNull
    @NotBlank
    @NotEmpty
    private String title;

    @NotNull
    private LocalDateTime plannedEndDate;

    @NotNull
    List<TaskFormDTO> tasks = new ArrayList<>();

    @NotNull
    private UUID supportGroupId;
}
