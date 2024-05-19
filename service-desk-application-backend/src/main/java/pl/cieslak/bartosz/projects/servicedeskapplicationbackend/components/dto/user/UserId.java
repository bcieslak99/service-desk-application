package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.user;

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
public class UserId
{
    @NotNull
    private UUID userId;
}
