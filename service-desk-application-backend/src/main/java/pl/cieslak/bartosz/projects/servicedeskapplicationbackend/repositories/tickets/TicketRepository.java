package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.tickets;

import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.Ticket;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketStatus;

import java.util.List;
import java.util.UUID;

public interface TicketRepository
{
    Ticket saveAndFlush(Ticket ticket);
    List<Ticket> getUserTicketsByUserIdAndTicketStatus(UUID userId,List<TicketStatus> statuses);
}
