package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import static pl.cieslak.bartosz.projects.servicedeskapplicationbackend.configuration.data.validation.UserDataValidation.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Component
@Entity
@Table(name = "USERS")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "USER_ID", nullable = false, unique = true, updatable = false)
    private UUID id;

    @NotNull
    @NotBlank
    @Column(name = "NAME")
    @Size(min = MINIMUM_LENGTH_OF_USER_NAME, max = MAXIMUM_LENGTH_OF_USER_NAME)
    private String name;

    @NotNull
    @NotBlank
    @Column(name = "SURNAME")
    @Size(min = MINIMUM_LENGTH_OF_USER_SURNAME, max = MAXIMUM_LENGTH_OF_USER_SURNAME)
    private String surname;

    @Email
    @NotNull
    @NotBlank
    @Size(min = MINIMUM_LENGTH_OF_USER_MAIL, max = MAXIMUM_LENGTH_OF_USER_MAIL)
    @Column(name = "MAIL", unique = true)
    private String mail;

    @Column(name = "ACCOUNT_IS_ACTIVE", nullable = false)
    private boolean active;

    @Column(name = "ACCESS_AS_EMPLOYEE_IS_PERMITTED", nullable = false)
    private boolean accessAsEmployeeIsPermitted = false;

    @NotNull
    @NotBlank
    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "LAST_PASSWORD_UPDATE", nullable = false)
    private LocalDateTime lastPasswordUpdate = LocalDateTime.now();

    @CreationTimestamp
    @Column(name = "ACCOUNT_CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime accountCreatedAt = LocalDateTime.now();

    @Column(name = "USER_IS_ADMINISTRATOR", nullable = false)
    private boolean administrator = false;

    @Size(max = MAXIMUM_LENGTH_OF_PHONE_NUMBER)
    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;
}
