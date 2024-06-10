package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

import static pl.cieslak.bartosz.projects.servicedeskapplicationbackend.configuration.data.validation.TicketActivityDataValidation.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Component
@Entity
@Table(name = "TICKETS_ACTIVITIES")
public class TicketActivity
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = false)
    private UUID id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(updatable = false)
    private TicketActivityType ticketActivityType;

    @NotNull
    @NotBlank
    @Size(min = MINIMUM_LENGTH_OF_ACTIVITY_DESCRIPTION, max = MAXIMUM_LENGTH_OF_ACTIVITY_DESCRIPTION)
    private String description;

    @NotNull
    @Column(updatable = false)
    private LocalDateTime activityDate;

    @ManyToOne
    @JoinColumn(name = "TICKET", nullable = false)
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "ANALYST")
    private User analyst;

    @Column(name = "USER_CAN_SEE", nullable = false)
    private boolean userCanSee = false;
}
