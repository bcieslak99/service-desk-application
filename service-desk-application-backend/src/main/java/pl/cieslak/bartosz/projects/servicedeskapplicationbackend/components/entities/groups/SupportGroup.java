package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.groups;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.stereotype.Component;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.groups.GroupDetailsDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tasks.TaskSet;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.Ticket;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketCategory;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static pl.cieslak.bartosz.projects.servicedeskapplicationbackend.configuration.data.validation.GroupDataValidation.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
@Entity
@Table(name = "SUPPORT_GROUPS")
public class SupportGroup
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @NotBlank
    @Size(min = MINIMUM_LENGTH_OF_GROUP_NAME, max = MAXIMUM_LENGTH_OF_GROUP_NAME)
    private String name;

    @NotNull
    @NotBlank
    @Size(min = MINIMUM_LENGTH_OF_GROUP_DESCRIPTION, max = MAXIMUM_LENGTH_OF_GROUP_DESCRIPTION)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    private GroupType groupType;

    @Column(name = "group_is_active", nullable = false)
    private boolean groupActive;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User groupManager;

    @ManyToMany
    @JoinTable(
            name = "group_members",
            joinColumns = {@JoinColumn(name = "group_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private List<User> groupMembers = new ArrayList<>();

    @OneToMany(mappedBy = "assigneeGroup")
    private List<Ticket> tickets = new ArrayList<>();

    @OneToMany(mappedBy = "defaultGroup")
    private List<TicketCategory> ownedCategories = new ArrayList<>();

    @OneToMany(mappedBy = "group")
    private List<TaskSet> taskSets = new ArrayList<>();

    public GroupDetailsDTO prepareGroupDetails()
    {
        return GroupDetailsDTO
                .builder()
                .groupId(this.id)
                .name(this.name)
                .description(this.description)
                .groupType(this.groupType)
                .groupActive(this.groupActive)
                .manager(this.groupManager != null ? this.groupManager.prepareUserContact() : null)
                .build();
    }
}
