package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.stereotype.Component;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.note.NoteDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

import static pl.cieslak.bartosz.projects.servicedeskapplicationbackend.configuration.data.validation.NoteDataValidation.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Component
@Entity
@Table(name = "Notes")
public class Note
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, nullable = false)
    private UUID id;

    @NotBlank
    @NotEmpty
    @NotNull
    @Column(name = "TITLE", nullable = false)
    @Size(min = MINIMUM_LENGTH_OF_NOTE_TITLE, max = MAXIMUM_LENGTH_OF_NOTE_TITLE)
    private String title;

    @NotBlank
    @NotEmpty
    @NotNull
    @Column(name = "DESCRIPTION", nullable = false)
    @Size(min = MINIMUM_LENGTH_OF_NOTE_DESCRIPTION, max = MAXIMUM_LENGTH_OF_NOTE_DESCRIPTION)
    private String description;

    @NotNull
    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false, nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    @UpdateTimestamp
    @Column(name = "LAST_UPDATE_AT", nullable = false)
    private LocalDateTime lastUpdateAt = LocalDateTime.now();

    @Column(name = "IS_DELETED", nullable = false)
    private boolean deleted = false;

    @ManyToOne
    @JoinColumn(name = "OWNER", nullable = false)
    private User owner;

    @PrePersist
    private void changeUpdateDate()
    {
        this.lastUpdateAt = LocalDateTime.now();
    }

    public NoteDTO createNoteDetails()
    {
        return NoteDTO
                .builder()
                .id(this.id)
                .title(this.title)
                .description(this.description)
                .build();
    }
}
