package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.jwt;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.auth.*;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.responses.ResponseCode;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.responses.ResponseMessage;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.user.NewUserDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.users.IncorrectPasswordException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.users.UserNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.user.UserRepository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.user.UserService;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService
{
    private static final String BAD_USERNAME_OR_PASSWORD_MESSAGE = "Podano zły login lub hasło.";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Nieoczekiwany błąd serwera.";
    private static final String INCORRECT_TOKEN_MESSAGE = "Token jest nieprawidłowy.";
    private static final String TOKEN_IS_ACTIVE_MESSAGE = "Token jest aktywny.";
    private static final String TOKEN_IS_INACTIVE_MESSAGE = "Token jest nieaktywny.";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final UserRepository USER_REPOSITORY;
    private final UserService USER_SERVICE;
    private final PasswordEncoder PASSWORD_ENCODER;
    private final AuthenticationManager AUTHENTICATION_MANAGER;
    private final JWTService JWT_SERVICE;

    public boolean userExists(String mail)
    {
        return this.USER_SERVICE.userExists(mail);
    }

    public User registerNewUser(NewUserDTO userData)
    {
        if(userData == null || USER_SERVICE.userExists(userData.getMail().trim())) return null;

        User user = new User();
        user.setMail(userData.getMail().trim().toLowerCase());
        user.setName(userData.getName().trim());
        user.setSurname(userData.getSurname().trim());
        user.setPassword(this.PASSWORD_ENCODER.encode(userData.getPassword().trim()));
        user.setActive(userData.isActive());
        user.setAccessAsEmployeeIsPermitted(userData.isAccessAsEmployeeIsPermitted());
        user.setAdministrator(userData.isAdministrator());

        if(userData.getPhoneNumber() == null) user.setPhoneNumber(null);
        else if(userData.getPhoneNumber().trim().isEmpty()) user.setPhoneNumber(null);
        else user.setPhoneNumber(userData.getPhoneNumber().trim());

        try
        {
            return this.USER_REPOSITORY.saveAndFlush(user);
        }
        catch(Exception exception)
        {
            return null;
        }
    }

    private boolean authenticate(AuthRequest authRequest)
    {
        if(authRequest == null || authRequest.getMail() == null || authRequest.getMail().trim().isEmpty()
                || authRequest.getPassword() == null || authRequest.getPassword().trim().isEmpty()) return false;

        Authentication authentication = this.AUTHENTICATION_MANAGER
                .authenticate(new UsernamePasswordAuthenticationToken(authRequest.getMail().trim(),
                        authRequest.getPassword().trim()));

        return authentication.isAuthenticated();
    }

    public ResponseEntity<?> authenticateAndGetToken(AuthRequest authRequest)
    {
        try
        {
            if(authenticate(authRequest))
            {
                Optional<User> userInDatabase = this.USER_REPOSITORY.getUserByMail(authRequest.getMail().trim());

                if(userInDatabase.isPresent())
                {
                    User user = userInDatabase.get();

                    AuthData tokenResponse = new AuthData();

                    tokenResponse.setToken(this.JWT_SERVICE.createToken(user));
                    tokenResponse.setRefreshToken(this.JWT_SERVICE.createRefreshToken(user));

                    UserDetails userDetails = new UserEntityDetails(user);
                    userDetails.getAuthorities().forEach(authority -> tokenResponse.getRoles().add(authority.getAuthority()));
                    tokenResponse.setName(user.getName());
                    tokenResponse.setSurname(user.getSurname());
                    tokenResponse.setMail(user.getMail());
                    return ResponseEntity.ok(tokenResponse);
                }
                else
                {
                    ResponseMessage responseMessage = new ResponseMessage(BAD_USERNAME_OR_PASSWORD_MESSAGE, ResponseCode.ERROR);
                    return ResponseEntity.badRequest().body(responseMessage);
                }
            }
            else
            {
                ResponseMessage responseMessage = new ResponseMessage(BAD_USERNAME_OR_PASSWORD_MESSAGE, ResponseCode.ERROR);
                return ResponseEntity.badRequest().body(responseMessage);
            }
        }
        catch(UsernameNotFoundException | DisabledException | BadCredentialsException exception)
        {
            ResponseMessage responseMessage = new ResponseMessage(BAD_USERNAME_OR_PASSWORD_MESSAGE, ResponseCode.ERROR);
            return ResponseEntity.badRequest().body(responseMessage);
        }
        catch(Exception exception)
        {
            ResponseMessage responseMessage = new ResponseMessage(INTERNAL_SERVER_ERROR_MESSAGE, ResponseCode.ERROR);
            return ResponseEntity.internalServerError().body(responseMessage);
        }
    }

    private String extractToken(HttpServletRequest request)
    {
        if(request == null) return null;

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if(authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith(TOKEN_PREFIX)) return null;

        return authHeader.substring(TOKEN_PREFIX.length()).trim();
    }

    public ResponseEntity<?> refreshToken(JWTToken authData)
    {
        UUID userId = JWT_SERVICE.extractUserIdFromToken(authData.getToken());

        if(userId == null)
        {
            TokenValidationResponse response = new TokenValidationResponse(INCORRECT_TOKEN_MESSAGE, TokenStatus.INACTIVE);
            return ResponseEntity.badRequest().body(response);
        }

        Optional<User> userInDatabase = this.USER_REPOSITORY.findById(userId);

        if(userInDatabase.isEmpty())
        {
            TokenValidationResponse response = new TokenValidationResponse(INCORRECT_TOKEN_MESSAGE, TokenStatus.INACTIVE);
            return ResponseEntity.badRequest().body(response);
        }

        User user = userInDatabase.get();

        AuthData token = new AuthData();
        token.setToken(this.JWT_SERVICE.createToken(user));
        token.setRefreshToken(this.JWT_SERVICE.createRefreshToken(user));
        UserDetails userDetails = new UserEntityDetails(user);
        userDetails.getAuthorities().forEach(authority -> token.getRoles().add(authority.getAuthority()));
        token.setName(user.getName());
        token.setSurname(user.getSurname());
        token.setMail(user.getMail());
        return ResponseEntity.ok(token);
    }

    public ResponseEntity<TokenValidationResponse> validateToken(HttpServletRequest request)
    {
        String token = extractToken(request);

        if(token == null || token.isEmpty())
        {
            TokenValidationResponse response = new TokenValidationResponse(INCORRECT_TOKEN_MESSAGE, TokenStatus.INACTIVE);
            return ResponseEntity.badRequest().body(response);
        }

        if(this.JWT_SERVICE.validateToken(token))
        {
            TokenValidationResponse response = new TokenValidationResponse(TOKEN_IS_ACTIVE_MESSAGE, TokenStatus.ACTIVE);
            return ResponseEntity.ok(response);
        }
        else
        {
            TokenValidationResponse response = new TokenValidationResponse(TOKEN_IS_INACTIVE_MESSAGE, TokenStatus.INACTIVE);
            return ResponseEntity.ok(response);
        }
    }

    public ResponseEntity<ResponseMessage> changeUSerPassword(UUID userId, String password)
    {
        try
        {
            Optional<User> userInDataBase = this.USER_REPOSITORY.getUserById(userId);

            if(userInDataBase.isEmpty())
                throw new UserNotFoundException("Nie odnaleziono wskazanego użytkownika!");

            if(password == null || password.trim().isEmpty())
                throw new IncorrectPasswordException("Nowe hasło nie spełnia wymagań!");

            User user = userInDataBase.get();
            user.setPassword(this.PASSWORD_ENCODER.encode(password));
            this.USER_REPOSITORY.saveAndFlush(user);

            return ResponseEntity.ok(new ResponseMessage("Hasło zostało zmienione.", ResponseCode.SUCCESS));
        }
        catch(UserNotFoundException | IncorrectPasswordException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage("Napotkano na nieoczekiwany błąd!", ResponseCode.ERROR));
        }
    }
}
