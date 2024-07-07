package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.system;

public class PermissionDeniedException extends Exception
{
    public PermissionDeniedException(String message)
    {
        super(message);
    }
}
