package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.note.NoteDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.note.NoteFormDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.Note;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.note.NoteException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.note.NoteNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.system.PermissionDeniedException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.users.UserNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.note.NoteRepository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.user.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoteService
{
    private static final String USER_NOT_FOUND_MESSAGE = "Nie rozpoznano Twojego konta!";
    private static final String NOTE_NOT_FOUND_MESSAGE = "Nie odnaleziono wskazanej notatki";

    private final NoteRepository NOTE_REPOSITORY;
    private final UserService USER_SERVICE;

    public void createNote(Principal principal, NoteFormDTO noteToCreate) throws Exception
    {
        if(principal == null || principal.getName() == null || principal.getName().trim().isEmpty())
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        Optional<User> userInDatabase = this.USER_SERVICE.getUserById(this.USER_SERVICE.extractUserId(principal));
        if(userInDatabase.isEmpty()) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);
        User owner = userInDatabase.get();

        Note note = new Note();
        note.setTitle(noteToCreate.getTitle().trim());
        note.setDescription(noteToCreate.getDescription().trim());
        note.setOwner(owner);

        this.NOTE_REPOSITORY.saveAndFlush(note);
    }

    public List<NoteDTO> getUserNotes(Principal principal) throws Exception
    {
        if(principal == null || principal.getName() == null || principal.getName().trim().isEmpty())
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        List<Note> notes = this.NOTE_REPOSITORY.getNotesByUserId(this.USER_SERVICE.extractUserId(principal));
        ArrayList<NoteDTO> details = new ArrayList<>();
        if(notes.size() > 0) details.ensureCapacity(notes.size());

        notes.forEach(note -> details.add(note.createNoteDetails()));

        return details;
    }

    public void deleteNote(Principal principal, UUID noteId) throws Exception
    {
        if(principal == null || principal.getName() == null || principal.getName().trim().isEmpty())
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        if(noteId == null) throw new NoteNotFoundException(NOTE_NOT_FOUND_MESSAGE);

        Optional<User> userInDatabase = this.USER_SERVICE.getUserById(this.USER_SERVICE.extractUserId(principal));
        if(userInDatabase.isEmpty()) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);
        User user = userInDatabase.get();

        Optional<Note> noteInDatabase = this.NOTE_REPOSITORY.getNoteById(noteId);
        if(noteInDatabase.isEmpty()) throw new NoteNotFoundException(NOTE_NOT_FOUND_MESSAGE);

        Note note = noteInDatabase.get();

        if(!note.getOwner().getId().equals(user.getId()))
            throw new PermissionDeniedException("Ta notatka nie należy do Ciebie!");

        note.setDeleted(true);
        this.NOTE_REPOSITORY.saveAndFlush(note);
    }

    public void editNote(Principal principal, UUID noteId, NoteFormDTO noteToEdit) throws Exception
    {
        if(principal == null || principal.getName() == null || principal.getName().trim().isEmpty())
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        if(noteId == null) throw new NoteNotFoundException(NOTE_NOT_FOUND_MESSAGE);
        if(noteToEdit == null) throw new NoteException("Nie podano nowych danych dla notatki!");

        Optional<User> userInDatabase = this.USER_SERVICE.getUserById(this.USER_SERVICE.extractUserId(principal));
        if(userInDatabase.isEmpty()) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);
        User user = userInDatabase.get();

        Optional<Note> noteInDatabase = this.NOTE_REPOSITORY.getNoteById(noteId);
        if(noteInDatabase.isEmpty()) throw new NoteNotFoundException(NOTE_NOT_FOUND_MESSAGE);
        Note note = noteInDatabase.get();

        if(!note.getOwner().getId().equals(user.getId()))
            throw new PermissionDeniedException("Ta notatka nie należy do Ciebie!");

        note.setTitle(noteToEdit.getTitle());
        note.setDescription(noteToEdit.getDescription());
        this.NOTE_REPOSITORY.saveAndFlush(note);
    }
}
