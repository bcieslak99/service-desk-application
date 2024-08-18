package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.tickets;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketAttachment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
interface TicketAttachmentSQLRepository extends JpaRepository<TicketAttachment, UUID>, TicketAttachmentRepository
{
    @Query("select ta from TicketAttachment as ta where ta.id = :attachmentId and ta.deleted = false")
    Optional<TicketAttachment> getTicketAttachmentById(@Param("attachmentId") UUID attachmentId);

    @Query("select ta from TicketAttachment as ta left join fetch ta.ticket as t where t.id = :ticketId and ta.deleted = false order by ta.createdAt desc")
    List<TicketAttachment>getAttachmentsListFromTicket(@Param("ticketId") UUID ticketId);
}
