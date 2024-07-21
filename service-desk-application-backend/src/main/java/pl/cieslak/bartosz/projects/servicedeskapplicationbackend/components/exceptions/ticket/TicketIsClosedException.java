package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.ticket;

public class TicketIsClosedException extends Exception
{
    public TicketIsClosedException(String message)
    {
        super(message);
    }
}
