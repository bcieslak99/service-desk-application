package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.user.UserContactDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.user.UserRepository;

import java.util.Optional;
import java.util.UUID;

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

    public Optional<User> getUserById(UUID id)
    {
        return this.USER_REPOSITORY.findById(id);
    }

    public boolean userExists(String mail)
    {
        return getUserByMail(mail).isPresent();
    }

    public UserContactDTO prepareUserAsContact(User user)
    {
        if(user == null) return null;

        return UserContactDTO.builder()
                .name(user.getName())
                .surname(user.getSurname())
                .mail(user.getMail())
                .userActive(user.isActive())
                .build();
    }
}
