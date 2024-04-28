package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.auth;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AuthResponse
{
    private String name;
    private String surname;
    private String mail;
    private JWTToken token;
    private JWTToken refreshToken;
    private List<String> roles = new ArrayList<>();
}
