package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.groups;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GroupExistsException extends Exception
{
    public GroupExistsException(String message)
    {
        super(message);
    }
}
