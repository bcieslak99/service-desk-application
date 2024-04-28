package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.auth;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TokenValidationResponse
{
    private String message;
    private TokenStatus tokenStatus;
}
