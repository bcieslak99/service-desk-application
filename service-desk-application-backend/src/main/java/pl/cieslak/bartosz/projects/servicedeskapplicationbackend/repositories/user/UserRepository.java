package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.user;

import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository
{
    Optional<User> getUserByMail(String mail);
    Optional<User> findById(UUID id);
    User saveAndFlush(User user);
    List<User> getAllUsers();
    Optional<User> getUserById(UUID userId);
}
