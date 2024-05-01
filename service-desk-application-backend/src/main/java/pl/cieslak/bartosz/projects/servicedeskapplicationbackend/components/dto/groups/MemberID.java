package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.groups;

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
public class MemberID
{
    @NotNull
    private UUID userId;
}
