package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.user.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService
{
    private final UserRepository USER_REPOSITORY;

    public Optional<User> getUserByMail(String mail)
    {
        if(mail == null || mail.trim().isEmpty()) return Optional.empty();
        return this.USER_REPOSITORY.getUserByMail(mail);
    }

    public boolean userExists(String mail)
    {
        return getUserByMail(mail).isPresent();
    }
}
