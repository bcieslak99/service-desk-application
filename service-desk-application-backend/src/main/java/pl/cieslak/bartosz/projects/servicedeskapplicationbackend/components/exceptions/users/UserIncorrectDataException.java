package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.users;

public class UserIncorrectDataException extends Exception
{
    public UserIncorrectDataException(String message)
    {
        super(message);
    }
}
