package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.note;

public class NoteNotFoundException extends Exception
{
    public NoteNotFoundException(String message)
    {
        super(message);
    }
}
