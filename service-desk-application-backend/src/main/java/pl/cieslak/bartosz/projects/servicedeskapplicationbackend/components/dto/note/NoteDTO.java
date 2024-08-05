package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class NoteDTO
{
    @NotNull
    private UUID id;

    @NotNull
    @NotEmpty
    @NotBlank
    private String title;

    @NotNull
    @NotEmpty
    @NotBlank
    private String description;

}
