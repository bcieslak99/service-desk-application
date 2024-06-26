package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.tickets;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.Ticket;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketStatus;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketType;

import java.util.List;
import java.util.UUID;

@Repository
interface TicketSQLRepository extends JpaRepository<Ticket, UUID>, TicketRepository
{
    @Query("select t from Ticket as t left join fetch t.reporter as r where t.status in (:statuses) and r.id = :id")
    List<Ticket> getUserTicketsByUserIdAndTicketStatus(@Param("id") UUID userId, @Param("statuses") List<TicketStatus> statuses);

    @Query("select t from Ticket as t left join fetch t.category as c left join fetch t.reporter as r left join fetch t.customer as cu where r.id = :userId and t.ticketType = :ticketType and t.status = :ticketStatus")
    List<Ticket> getTicketsByTypeAndStatus(@Param("userId") UUID userId, @Param("ticketType") TicketType ticketType, @Param("ticketStatus") TicketStatus ticketStatus);
}
