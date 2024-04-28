package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.auth;

import lombok.*;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class AuthRequest
{
    private String mail;
    private String password;
}
