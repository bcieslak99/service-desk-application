package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.users;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserNotFoundException extends Exception
{
    public UserNotFoundException(String message)
    {
        super(message);
    }
}
