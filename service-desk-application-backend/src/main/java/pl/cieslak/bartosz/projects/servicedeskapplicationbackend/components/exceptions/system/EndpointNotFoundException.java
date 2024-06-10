package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.system;

public class EndpointNotFoundException extends Exception
{
    public EndpointNotFoundException(String message)
    {
        super(message);
    }
}
