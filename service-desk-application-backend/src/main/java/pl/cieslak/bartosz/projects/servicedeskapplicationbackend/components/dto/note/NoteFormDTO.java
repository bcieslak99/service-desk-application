package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.stereotype.Component;

import static pl.cieslak.bartosz.projects.servicedeskapplicationbackend.configuration.data.validation.NoteDataValidation.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class NoteFormDTO
{
    @NotBlank
    @NotEmpty
    @NotNull
    @Size(min = MINIMUM_LENGTH_OF_NOTE_TITLE, max = MAXIMUM_LENGTH_OF_NOTE_TITLE)
    private String title;

    @NotBlank
    @NotEmpty
    @NotNull
    @Size(min = MINIMUM_LENGTH_OF_NOTE_DESCRIPTION, max = MAXIMUM_LENGTH_OF_NOTE_DESCRIPTION)
    private String description;
}
