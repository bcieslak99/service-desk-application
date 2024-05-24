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
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.categories.CategoryDetailsForEmployeeDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.groups.SupportGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static pl.cieslak.bartosz.projects.servicedeskapplicationbackend.configuration.data.validation.TicketCategoryDataValidation.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Component
@Entity
@Table(name = "TICKETS_CATEGORIES")
public class TicketCategory
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @NotBlank
    @Size(min = MINIMUM_LENGTH_OF_CATEGORY_NAME, max = MAXIMUM_LENGTH_OF_CATEGORY_NAME)
    private String name;

    @NotNull
    @NotBlank
    @Size(min = MINIMUM_LENGTH_OF_CATEGORY_DESCRIPTION, max = MAXIMUM_LENGTH_OF_CATEGORY_DESCRIPTION)
    private String description;

    @Column(nullable = false)
    private boolean categoryIsActive;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketType ticketType;

    @OneToMany(mappedBy = "category")
    private List<Ticket> tickets = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "default_group", nullable = false)
    private SupportGroup defaultGroup;

    public CategoryDetailsForEmployeeDTO prepareCategoryDetailsForEmployee()
    {
        return CategoryDetailsForEmployeeDTO.builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .build();
    }
}
