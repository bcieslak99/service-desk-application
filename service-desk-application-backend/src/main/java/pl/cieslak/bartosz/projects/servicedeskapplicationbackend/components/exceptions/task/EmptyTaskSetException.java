package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.task;

public class EmptyTaskSetException extends Exception
{
    public EmptyTaskSetException(String message)
    {
        super(message);
    }
}
