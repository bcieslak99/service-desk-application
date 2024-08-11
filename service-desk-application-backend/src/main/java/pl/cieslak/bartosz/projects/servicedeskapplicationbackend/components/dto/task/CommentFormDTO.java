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
public class CommentFormDTO
{
    @NotNull
    @NotEmpty
    @NotBlank
    private String comment;
}
