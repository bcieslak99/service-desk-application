package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.groups;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GroupNotFoundException extends Exception
{
    public GroupNotFoundException(String message)
    {
        super(message);
    }
}
