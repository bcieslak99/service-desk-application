package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.controllers.api.v1;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.auth.AuthRequest;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.auth.JWTToken;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.responses.ResponseCode;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.responses.ResponseMessage;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.user.NewUserDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.jwt.AuthService;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/auth")
public class AuthController
{
    private final static String INCORRECT_USER_DATA_MESSAGE = "Dane użytkownika są nieprawidłowe.";
    private final static String REGISTERED_USER_MESSAGE = "Użytkownik został zarejestrowany.";
    private final static String USER_EXISTS_MESSAGE = "Użytkownik z takim adresem mail już istnieje.";
    private final static String REGISTRATION_ERROR_MESSAGE = "Wystąpił błąd podczas rejestracji.";

    private final AuthService AUTH_SERVICE;

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<ResponseMessage> registerUser(@Valid @RequestBody NewUserDTO userData, BindingResult errors)
    {
        if(errors.hasErrors())
        {
            ResponseMessage responseMessage = new ResponseMessage(INCORRECT_USER_DATA_MESSAGE, ResponseCode.ERROR);
            return ResponseEntity.badRequest().body(responseMessage);
        }

        if(this.AUTH_SERVICE.userExists(userData.getMail()))
        {
            ResponseMessage responseMessage = new ResponseMessage(USER_EXISTS_MESSAGE, ResponseCode.ERROR);
            return ResponseEntity.badRequest().body(responseMessage);
        }

        User user = this.AUTH_SERVICE.registerNewUser(userData);

        if(user != null)
        {
            ResponseMessage responseMessage = new ResponseMessage(REGISTERED_USER_MESSAGE, ResponseCode.SUCCESS);
            return ResponseEntity.ok(responseMessage);
        }
        else
        {
            ResponseMessage responseMessage = new ResponseMessage(REGISTRATION_ERROR_MESSAGE, ResponseCode.ERROR);
            return ResponseEntity.badRequest().body(responseMessage);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequest authRequest)
    {
        return this.AUTH_SERVICE.authenticateAndGetToken(authRequest);
    }

    @GetMapping("/token/validation")
    public ResponseEntity<?> validateToken(HttpServletRequest request)
    {
        return this.AUTH_SERVICE.validateToken(request);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody JWTToken authData)
    {
        return this.AUTH_SERVICE.refreshToken(authData);
    }
}
