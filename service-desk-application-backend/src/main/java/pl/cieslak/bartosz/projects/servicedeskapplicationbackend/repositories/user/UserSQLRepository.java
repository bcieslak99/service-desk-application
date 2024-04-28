package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;

import java.util.Optional;
import java.util.UUID;

@Repository
interface UserSQLRepository extends JpaRepository<User, UUID>, UserRepository
{
    Optional<User> getUserByMail(String mail);
}
