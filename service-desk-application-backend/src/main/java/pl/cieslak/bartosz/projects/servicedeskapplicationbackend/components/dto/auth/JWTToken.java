package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Component
public class JWTToken
{
    private String token;
    private long expiration;
}
