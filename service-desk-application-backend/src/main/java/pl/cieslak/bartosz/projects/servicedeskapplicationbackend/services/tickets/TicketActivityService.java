package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.tickets;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.tickets.TicketActivitiesRepository;

@Service
@RequiredArgsConstructor
public class TicketActivityService
{
    private final TicketActivitiesRepository TICKET_ACTIVITY_REPOSITORY;
}
