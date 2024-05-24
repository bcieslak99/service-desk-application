package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Component;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.user.UserDetailsDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.groups.SupportGroup;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.Ticket;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketActivity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static pl.cieslak.bartosz.projects.servicedeskapplicationbackend.configuration.data.validation.UserDataValidation.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Component
@Entity
@Table(name = "USERS")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID id;

    @NotNull
    @NotBlank
    @Column(name = "NAME")
    @Size(min = MINIMUM_LENGTH_OF_USER_NAME, max = MAXIMUM_LENGTH_OF_USER_NAME)
    private String name;

    @NotNull
    @NotBlank
    @Column(name = "SURNAME")
    @Size(min = MINIMUM_LENGTH_OF_USER_SURNAME, max = MAXIMUM_LENGTH_OF_USER_SURNAME)
    private String surname;

    @Email
    @NotNull
    @NotBlank
    @Size(min = MINIMUM_LENGTH_OF_USER_MAIL, max = MAXIMUM_LENGTH_OF_USER_MAIL)
    @Column(name = "MAIL", unique = true)
    private String mail;

    @Column(name = "ACCOUNT_IS_ACTIVE", nullable = false)
    private boolean active;

    @Column(name = "ACCESS_AS_EMPLOYEE_IS_PERMITTED", nullable = false)
    private boolean accessAsEmployeeIsPermitted = false;

    @NotNull
    @NotBlank
    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "LAST_PASSWORD_UPDATE", nullable = false)
    private LocalDateTime lastPasswordUpdate = LocalDateTime.now();

    @CreationTimestamp
    @Column(name = "ACCOUNT_CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime accountCreatedAt = LocalDateTime.now();

    @Column(name = "USER_IS_ADMINISTRATOR", nullable = false)
    private boolean administrator = false;

    @Size(max = MAXIMUM_LENGTH_OF_PHONE_NUMBER)
    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @OneToMany(mappedBy = "groupManager")
    private List<SupportGroup> ownedGroups = new ArrayList<>();

    @ManyToMany(mappedBy = "groupMembers")
    private List<SupportGroup> userGroups = new ArrayList<>();

    @OneToMany(mappedBy = "customer")
    private List<Ticket> ownTickets = new ArrayList<>();

    @OneToMany(mappedBy = "reporter")
    private List<Ticket> reportedTickets = new ArrayList<>();

    @OneToMany(mappedBy = "assigneeAnalyst")
    private List<Ticket> assignedTickets = new ArrayList<>();

    @OneToMany(mappedBy = "analyst")
    private List<TicketActivity> ticketActivities = new ArrayList<>();

    public UserDetailsDTO prepareUserDetails()
    {
        return UserDetailsDTO.builder()
                .userId(this.getId())
                .name(this.getName())
                .surname(this.getSurname())
                .mail(this.getMail())
                .phoneNumber(this.getPhoneNumber())
                .userActive(this.isActive())
                .build();
    }
}
