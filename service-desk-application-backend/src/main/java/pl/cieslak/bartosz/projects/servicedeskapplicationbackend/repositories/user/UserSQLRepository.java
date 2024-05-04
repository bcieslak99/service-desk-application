package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
interface UserSQLRepository extends JpaRepository<User, UUID>, UserRepository
{
    @Query("select u from User as u left join fetch u.userGroups as ug where u.mail = :mail")
    Optional<User> getUserByMail(@Param("mail") String mail);

    @Query("select u from User as u left join fetch u.userGroups as ug where u.id = :id")
    Optional<User> getUserById(@Param("id") UUID userId);

    @Query("select u from User as u order by u.surname, u.name, u.mail")
    List<User> getAllUsers();
}
