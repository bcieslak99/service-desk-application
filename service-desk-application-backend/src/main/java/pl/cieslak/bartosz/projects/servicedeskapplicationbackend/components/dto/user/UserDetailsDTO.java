package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.user;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class UserDetailsDTO
{
    private UUID userId;
    private String name;
    private String surname;
    private String mail;
    private String phoneNumber;
    private boolean userActive;
}
