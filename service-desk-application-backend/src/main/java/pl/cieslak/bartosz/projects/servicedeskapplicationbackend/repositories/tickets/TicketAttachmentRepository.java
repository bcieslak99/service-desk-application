package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.tickets;

import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketAttachment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketAttachmentRepository
{
    TicketAttachment saveAndFlush(TicketAttachment ticketAttachment);
    Optional<TicketAttachment> getTicketAttachmentById(UUID attachmentId);
    List<TicketAttachment> getAttachmentsListFromTicket(UUID ticketId);
}
