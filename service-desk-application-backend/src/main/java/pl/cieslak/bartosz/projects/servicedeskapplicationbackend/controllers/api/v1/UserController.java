package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.controllers.api.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.responses.ResponseCode;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.responses.ResponseMessage;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.user.NewUserDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.user.UserDetailsDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.user.UserNewDataDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.user.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/user")
public class UserController
{
    private final UserService USER_SERVICE;

    @GetMapping("/list/all")
    @PreAuthorize("hasAuthority('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<List<UserDetailsDTO>> getAllUsers()
    {
        return ResponseEntity.ok(this.USER_SERVICE.getAllUsers());
    }

    @PatchMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<ResponseMessage> editUserData(@PathVariable("id") UUID userId,
                                                        @Valid @RequestBody UserNewDataDTO userData,
                                                        BindingResult errors)
    {
        if(errors.hasErrors())
            return ResponseEntity.badRequest()
                    .body(new ResponseMessage("Dane do aktualizacji są nieprawidłowe!", ResponseCode.ERROR));

        return this.USER_SERVICE.editUserData(userId, userData);
    }
}
