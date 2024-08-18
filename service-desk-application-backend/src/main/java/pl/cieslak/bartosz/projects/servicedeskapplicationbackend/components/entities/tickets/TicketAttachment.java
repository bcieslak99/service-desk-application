package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.attachment.AttachmentDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Component
@Entity
@Table(name = "ATTACHMENTS")
public class TicketAttachment
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(updatable = false, nullable = false)
    @Size(min = 3, max = 250)
    @NotEmpty
    @NotBlank
    @NotNull
    private String name;

    @Column(name = "ADDED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private boolean deleted = false;

    @ManyToOne
    @JoinColumn(name = "TICKET_ID", nullable = false)
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "FILE_OWNER")
    private User fileOwner;

    public AttachmentDTO prepareDetails()
    {
        return AttachmentDTO
                .builder()
                .id(this.id)
                .name(this.name)
                .build();
    }
}
