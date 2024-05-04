package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.user;

import jakarta.validation.constraints.Email;
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
public class UserNewDataDTO
{
    @NotNull
    @NotBlank
    @Size(min = MINIMUM_LENGTH_OF_USER_NAME, max = MAXIMUM_LENGTH_OF_USER_NAME)
    private String name;

    @NotNull
    @NotBlank
    @Size(min = MINIMUM_LENGTH_OF_USER_SURNAME, max = MAXIMUM_LENGTH_OF_USER_SURNAME)
    private String surname;

    @Email
    @NotNull
    @NotBlank
    @Size(min = MINIMUM_LENGTH_OF_USER_MAIL, max = MAXIMUM_LENGTH_OF_USER_MAIL)
    private String mail;

    private boolean active;

    private boolean administrator;

    private boolean accessAsEmployeeIsPermitted = false;

    @Size(max = MAXIMUM_LENGTH_OF_PHONE_NUMBER)
    private String phoneNumber;
}
