package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.auth.UserEntityDetails;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.user.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserEntityDetailsService implements UserDetailsService
{
    private static final String USER_NOT_FOUND_MESSAGE = "Nie odnaleziono użytkownika!";
    private final UserRepository USER_REPOSITORY;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        Optional<User> userInDatabase = this.USER_REPOSITORY.getUserByMail(username.trim().toLowerCase());
        return userInDatabase.map(UserEntityDetails::new).orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));
    }
}
