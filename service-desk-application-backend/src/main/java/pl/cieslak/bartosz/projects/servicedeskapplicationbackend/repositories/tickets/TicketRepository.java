package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.tickets;

import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.Ticket;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketStatus;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository
{
    Ticket saveAndFlush(Ticket ticket);
    List<Ticket> getUserTicketsByUserIdAndTicketStatus(UUID userId,List<TicketStatus> statuses);
    List<Ticket> getTicketsByTypeAndStatus(UUID userId, TicketType ticketType, TicketStatus ticketStatus);
    Optional<Ticket> getTicketGroupAndReporterByTicketId(UUID ticketId);
    Optional<Ticket> getTicketDetailsById(UUID ticketId);
    List<Ticket> getTicketOfMyGroupsByStatusAndTicketType(TicketStatus ticketStatus, TicketType ticketType, UUID userId);
    List<Ticket> getUserTicketsByStatusAndType(TicketStatus ticketStatus, TicketType ticketType, UUID userId);
}
