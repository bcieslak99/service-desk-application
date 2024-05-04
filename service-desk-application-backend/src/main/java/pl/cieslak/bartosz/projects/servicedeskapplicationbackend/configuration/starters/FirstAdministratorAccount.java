package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.configuration.starters;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.user.NewUserDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.jwt.AuthService;

@Configuration
@RequiredArgsConstructor
public class FirstAdministratorAccount implements CommandLineRunner
{
    private final AuthService AUTH_SERVICE;
    private static final String ADMINISTRATOR_ACCOUNT_NAME = "root";
    public static final String ADMINISTRATOR_ACCOUNT_MAIL = "root@appliaction.local";
    private static final String ADMINISTRATOR_ACCOUNT_PASSWORD = "b1f9def3-8a0b-4679-88a3-8eaf6af7a8b0";

    @Override
    public void run(String... args) throws Exception
    {
        NewUserDTO newUser = NewUserDTO.builder()
                .name(ADMINISTRATOR_ACCOUNT_NAME)
                .surname(ADMINISTRATOR_ACCOUNT_NAME)
                .mail(ADMINISTRATOR_ACCOUNT_MAIL)
                .password(ADMINISTRATOR_ACCOUNT_PASSWORD)
                .active(true)
                .administrator(true)
                .accessAsEmployeeIsPermitted(true)
                .build();

        try
        {
            User user = null;

            if(!this.AUTH_SERVICE.userExists(ADMINISTRATOR_ACCOUNT_MAIL))
                user = this.AUTH_SERVICE.registerNewUser(newUser);

            if(user != null) System.out.println("Zarejestrowano konto administratora!");
        }
        catch(Exception exception)
        {
            System.out.println("Wystąpił błąd podczas rejestracji konta administratora!");
        }
    }
}
