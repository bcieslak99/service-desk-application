package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.responses.ResponseCode;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.responses.ResponseMessage;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.user.UserContactDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.user.UserDetailsDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.user.UserNewDataDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.users.AdministratorMailChangeException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.users.UserIncorrectDataException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.users.UserNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.configuration.starters.FirstAdministratorAccount;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.user.UserRepository;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService
{
    private final UserRepository USER_REPOSITORY;

    public UUID extractUserId(Principal principal)
    {
        try
        {
            if(principal == null || principal.getName() == null || principal.getName().trim().isEmpty()) return null;
            return UUID.fromString(principal.getName());
        }
        catch (Exception exception)
        {
            return null;
        }
    }

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
                .userId(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .mail(user.getMail())
                .userActive(user.isActive())
                .build();
    }

    public UserDetailsDTO prepareUserDetails(User user)
    {
        if(user == null) return null;

        return UserDetailsDTO.builder()
                .userId(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .mail(user.getMail())
                .phoneNumber(user.getPhoneNumber())
                .userActive(user.isActive())
                .build();
    }

    public List<UserDetailsDTO> getAllUsers()
    {
        List<User> usersInDatabase = this.USER_REPOSITORY.getAllUsers();
        List<UserDetailsDTO> users = new ArrayList<>();

        for(User user : usersInDatabase)
            users.add(prepareUserDetails(user));

        users = users.stream().filter(Objects::nonNull).collect(Collectors.toList());

        return users;
    }

    public List<UserDetailsDTO> getActiveUsers()
    {
        List<User> usersInDatabase = this.USER_REPOSITORY.getActiveUsers();
        List<UserDetailsDTO> users = new ArrayList<>();

        for(User user : usersInDatabase)
            users.add(prepareUserDetails(user));

        users = users.stream().filter(Objects::nonNull).collect(Collectors.toList());

        return users;
    }

    public ResponseEntity<ResponseMessage> editUserData(UUID userId, UserNewDataDTO userData)
    {
        try
        {
            if(userId == null) throw new UserNotFoundException("Podano nieprawidłowy identyfikator użytkownika!");
            if(userData == null) throw new UserIncorrectDataException("Podano nieprawidłowe dane użytkownika!");

            Optional<User> userInDatabase = this.USER_REPOSITORY.getUserByMail(userData.getMail().trim());

            if(userInDatabase.isPresent())
            {
                User user = userInDatabase.get();

                if(!user.getId().equals(userId))
                    throw new UserIncorrectDataException("Wskazany adres mail jest przypisany do innego użytkownika!");
            }

            userInDatabase = this.USER_REPOSITORY.findById(userId);
            if(userInDatabase.isEmpty()) throw new UserNotFoundException("Nie odnaleziono wskazanego użytkownika!");

            User user = userInDatabase.get();

            if(user.getMail().equals(FirstAdministratorAccount.ADMINISTRATOR_ACCOUNT_MAIL) &&
                    !userData.getMail().equals(FirstAdministratorAccount.ADMINISTRATOR_ACCOUNT_MAIL))
                throw new AdministratorMailChangeException("Nie można zmieniać adresu mail dla konta administratora!");

            user.setName(userData.getName().trim());
            user.setSurname(userData.getSurname().trim());
            user.setMail(userData.getMail().trim());
            user.setActive(userData.isActive());
            user.setAdministrator(userData.isAdministrator());
            user.setAccessAsEmployeeIsPermitted(userData.isAccessAsEmployeeIsPermitted());

            if(userData.getPhoneNumber() == null || userData.getPhoneNumber().trim().isEmpty())
                user.setPhoneNumber(null);
            else user.setPhoneNumber(userData.getPhoneNumber().trim());

            this.USER_REPOSITORY.saveAndFlush(user);

            return ResponseEntity.ok(new ResponseMessage("Dane użytkownika zostały edytowane.", ResponseCode.SUCCESS));
        }
        catch(UserNotFoundException | UserIncorrectDataException | AdministratorMailChangeException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError()
                    .body(new ResponseMessage("Napotkano na nieoczekiwany błąd podczas edycji danych użytkownika!",
                            ResponseCode.ERROR));
        }
    }

    public ResponseEntity<?> getUserDetails(UUID userId)
    {
        try
        {
            Optional<User> userInDatabase = this.USER_REPOSITORY.findById(userId);

            if(userInDatabase.isEmpty())
                throw new UserNotFoundException("Nie odnaleziono wskazanego użytkownika!");

            User user = userInDatabase.get();
            UserNewDataDTO details = UserNewDataDTO
                    .builder()
                    .name(user.getName())
                    .surname(user.getSurname())
                    .mail(user.getMail())
                    .active(user.isActive())
                    .administrator(user.isAdministrator())
                    .accessAsEmployeeIsPermitted(user.isAccessAsEmployeeIsPermitted())
                    .phoneNumber(user.getPhoneNumber())
                    .build();

            return ResponseEntity.ok(details);
        }
        catch(UserNotFoundException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage("Napotkano na nieoczekiwany błąd!", ResponseCode.ERROR));
        }
    }
}
