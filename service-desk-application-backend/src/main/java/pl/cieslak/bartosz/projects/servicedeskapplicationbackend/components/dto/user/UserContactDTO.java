package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class UserContactDTO
{
    @NotNull
    private String name;

    @NotNull
    private String surname;

    @NotNull
    private String mail;

    private String phoneNumber;

    private boolean userActive;
}
