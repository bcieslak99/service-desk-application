package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.categories;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class CategoryDetailsForEmployeeDTO
{
    private UUID id;
    private String name;
    private String description;
}
