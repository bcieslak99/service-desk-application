package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.users;

public class IncorrectPasswordException extends Exception
{
    public IncorrectPasswordException(String message)
    {
        super(message);
    }
}
