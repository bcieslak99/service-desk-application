package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.ticket;

public class TicketNotFoundException extends Exception
{
    public TicketNotFoundException(String message)
    {
        super(message);
    }
}
