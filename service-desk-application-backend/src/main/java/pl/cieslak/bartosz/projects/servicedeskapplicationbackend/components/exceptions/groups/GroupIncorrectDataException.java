package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.groups;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GroupIncorrectDataException extends Exception
{
    public GroupIncorrectDataException(String message)
    {
        super(message);
    }
}
