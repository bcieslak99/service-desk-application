package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.tickets;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.tickets.TicketRepository;

@Service
@RequiredArgsConstructor
public class TicketService
{
    private final TicketRepository TICKET_REPOSITORY;
}
