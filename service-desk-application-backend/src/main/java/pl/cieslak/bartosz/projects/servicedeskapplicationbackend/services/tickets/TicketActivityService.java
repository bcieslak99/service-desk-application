package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.tickets;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.Ticket;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketActivity;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketActivityType;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.ticket.TicketActivityException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.tickets.TicketActivitiesRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TicketActivityService
{
    private final TicketActivitiesRepository TICKET_ACTIVITY_REPOSITORY;

    private final static String ACTIVITY_CREATION_ERROR_MESSAGE = "Nie udało się dodać aktywności do zgłoszenia!";

    public void addCreationActivity(Ticket ticket, User createdBy) throws TicketActivityException
    {
        if(ticket == null || createdBy == null)
            throw new TicketActivityException(ACTIVITY_CREATION_ERROR_MESSAGE);

        TicketActivity activity = new TicketActivity();
        activity.setTicketActivityType(TicketActivityType.CREATE_TICKET);
        activity.setDescription("Zgłoszenie zostało utworzone przez " + createdBy.getSurname() + " " +
                createdBy.getName() + " (" + createdBy.getMail() + ")");
        activity.setTicket(ticket);
        activity.setAnalyst(createdBy);
        activity.setUserCanSee(true);
        activity.setActivityDate(LocalDateTime.now());
        this.TICKET_ACTIVITY_REPOSITORY.saveAndFlush(activity);
    }
}
