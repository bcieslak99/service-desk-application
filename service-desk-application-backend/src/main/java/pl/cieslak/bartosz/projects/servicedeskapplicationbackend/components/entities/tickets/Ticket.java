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
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.activities.TicketActivityAsListElement;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.ticket.TicketDetailsForAnalystDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.ticket.TicketDetailsForEmployeeDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.groups.SupportGroup;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static pl.cieslak.bartosz.projects.servicedeskapplicationbackend.configuration.data.validation.TicketDataValidation.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Component
@Entity
@Table(name = "TICKETS")
public class Ticket
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, nullable = false)
    private UUID id;

    @Column(name = "TYPE", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private TicketType ticketType;

    @NotNull
    @NotBlank
    @Size(min = MINIMUM_LENGTH_OF_DESCRIPTION, max = MAXIMUM_LENGTH_OF_DESCRIPTION)
    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "CUSTOMER", nullable = false)
    private User customer;

    @ManyToOne
    @JoinColumn(name = "REPORTER", nullable = false)
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "ASSIGNEE_ANALYST")
    private User assigneeAnalyst;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime openDate;

    private LocalDateTime resolveDate;

    private LocalDateTime closeDate;

    @ManyToOne
    @JoinColumn(name = "SUPPORT_GROUP", nullable = false)
    private SupportGroup assigneeGroup;

    @OneToMany(mappedBy = "ticket")
    private List<TicketActivity> ticketActivities = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "CATEGORY", nullable = false)
    private TicketCategory category;

    @OneToMany(mappedBy = "ticket")
    private List<TicketAttachment> attachments = new ArrayList<>();

    public TicketDetailsForEmployeeDTO prepareDetailsForEmployee()
    {
        TicketDetailsForEmployeeDTO details = TicketDetailsForEmployeeDTO.builder()
                .id(this.id)
                .ticketType(this.ticketType)
                .description(this.description)
                .customer(this.customer.prepareUserDetails())
                .reporter(this.reporter.prepareUserDetails())
                .status(this.status)
                .openDate(this.openDate)
                .resolveDate(this.resolveDate)
                .closeDate(this.closeDate)
                .category(this.category.prepareCategoryDetailsForEmployee())
                .build();

        List<TicketActivityAsListElement> activities = new ArrayList<>();
        this.ticketActivities.forEach(element -> {
            if(element.isUserCanSee())
            {
                TicketActivityAsListElement activity = new TicketActivityAsListElement();
                activity.setId(element.getId());
                activity.setTicketActivityType(element.getTicketActivityType());
                activity.setDescription(element.getDescription());
                activity.setActivityDate(element.getActivityDate());
                activity.setAnalyst(element.getAnalyst().prepareUserDetails());
                activities.add(activity);
            }
        });

        details.setActivities(activities);

        return details;
    }

    public TicketDetailsForAnalystDTO prepareDetailsForAnalyst()
    {
        TicketDetailsForAnalystDTO details = TicketDetailsForAnalystDTO.builder()
                .id(this.id)
                .ticketType(this.ticketType)
                .description(this.description)
                .customer(this.customer.prepareUserDetails())
                .reporter(this.reporter.prepareUserDetails())
                .assigneeAnalyst(this.assigneeAnalyst != null ? this.assigneeAnalyst.prepareUserDetails() : null)
                .assigneeGroup(this.assigneeGroup.prepareGroupDetails())
                .status(this.status)
                .openDate(this.openDate)
                .resolveDate(this.resolveDate)
                .closeDate(this.closeDate)
                .category(this.category.prepareCategoryDetailsForEmployee())
                .build();

        List<TicketActivityAsListElement> activities = new ArrayList<>();
        this.ticketActivities.forEach(element -> {
            TicketActivityAsListElement activity = new TicketActivityAsListElement();
            activity.setId(element.getId());
            activity.setTicketActivityType(element.getTicketActivityType());
            activity.setDescription(element.getDescription());
            activity.setActivityDate(element.getActivityDate());
            activity.setAnalyst(element.getAnalyst().prepareUserDetails());
            activities.add(activity);
        });

        details.setActivities(activities);

        return details;
    }
}
