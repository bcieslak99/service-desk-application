package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.groups;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GroupCreationException extends Exception
{
    public GroupCreationException(String message)
    {
        super(message);
    }
}
