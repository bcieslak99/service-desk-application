package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.note;

import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.Note;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteRepository
{
    Note saveAndFlush(Note note);
    List<Note> getNotesByUserId(UUID userId);
    Optional<Note> getNoteById(UUID noteId);
}
