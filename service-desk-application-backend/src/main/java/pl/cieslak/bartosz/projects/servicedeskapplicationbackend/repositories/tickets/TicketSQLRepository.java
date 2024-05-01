package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.tickets;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.Ticket;

import java.util.UUID;

@Repository
interface TicketSQLRepository extends JpaRepository<Ticket, UUID>, TicketRepository {}
