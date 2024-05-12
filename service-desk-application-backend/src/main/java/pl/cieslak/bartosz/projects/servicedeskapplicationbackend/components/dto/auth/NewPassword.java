package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.stereotype.Component;

import static pl.cieslak.bartosz.projects.servicedeskapplicationbackend.configuration.data.validation.UserDataValidation.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class NewPassword
{
    @NotNull
    @NotBlank
    @Size(min = MINIMUM_LENGTH_OF_USER_PASSWORD, max = MAXIMUM_LENGTH_OF_USER_PASSWORD)
    private String password;
}
