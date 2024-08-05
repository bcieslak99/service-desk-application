package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.controllers.api.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.note.NoteFormDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.responses.ResponseCode;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.responses.ResponseMessage;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.note.NoteNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.system.PermissionDeniedException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.users.UserNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.NoteService;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/note")
public class NoteController
{
    private final NoteService NOTE_SERVICE;

    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Napotkano na nieoczekiwany błąd!";

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('FIRST_LINE_ANALYST', 'SECOND_LINE_ANALYST')")
    public ResponseEntity<ResponseMessage> createNote(Principal principal, @Valid @RequestBody NoteFormDTO note, BindingResult errors)
    {
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(new ResponseMessage("Twoja notatka zawiera błędy!", ResponseCode.ERROR));

        try
        {
            this.NOTE_SERVICE.createNote(principal, note);
            return ResponseEntity.ok(new ResponseMessage("Notatka została utworzona", ResponseCode.SUCCESS));
        }
        catch(UserNotFoundException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage(INTERNAL_SERVER_ERROR_MESSAGE, ResponseCode.ERROR));
        }
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('FIRST_LINE_ANALYST', 'SECOND_LINE_ANALYST')")
    public ResponseEntity<?> getNotes(Principal principal)
    {
        try
        {
            return ResponseEntity.ok(this.NOTE_SERVICE.getUserNotes(principal));
        }
        catch(UserNotFoundException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage(INTERNAL_SERVER_ERROR_MESSAGE, ResponseCode.ERROR));
        }
    }

    @DeleteMapping("/delete/{noteId}")
    @PreAuthorize("hasAnyAuthority('FIRST_LINE_ANALYST', 'SECOND_LINE_ANALYST')")
    public ResponseEntity<ResponseMessage> deleteNote(Principal principal, @PathVariable("noteId") UUID noteId)
    {
        try
        {
            this.NOTE_SERVICE.deleteNote(principal, noteId);
            return ResponseEntity.ok(new ResponseMessage("Notatka została usunięta", ResponseCode.SUCCESS));
        }
        catch(UserNotFoundException | NoteNotFoundException | PermissionDeniedException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage(INTERNAL_SERVER_ERROR_MESSAGE, ResponseCode.ERROR));
        }
    }

    @PatchMapping("/edit/{noteId}")
    @PreAuthorize("hasAnyAuthority('FIRST_LINE_ANALYST', 'SECOND_LINE_ANALYST')")
    public ResponseEntity<ResponseMessage> editNote(Principal principal, @PathVariable UUID noteId, @Valid @RequestBody NoteFormDTO note, BindingResult errors)
    {
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(new ResponseMessage("Twoja notatka zawiera błędy!", ResponseCode.ERROR));

        try
        {
            this.NOTE_SERVICE.editNote(principal, noteId, note);
            return ResponseEntity.ok(new ResponseMessage("Notatka została zaktualizowana", ResponseCode.SUCCESS));
        }
        catch(UserNotFoundException | NoteNotFoundException | PermissionDeniedException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage(INTERNAL_SERVER_ERROR_MESSAGE, ResponseCode.ERROR));
        }
    }
}
