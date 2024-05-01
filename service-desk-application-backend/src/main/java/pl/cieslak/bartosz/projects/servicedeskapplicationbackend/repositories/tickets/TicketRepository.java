package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.tickets;

import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.Ticket;

public interface TicketRepository
{
    Ticket saveAndFlush(Ticket ticket);
}
