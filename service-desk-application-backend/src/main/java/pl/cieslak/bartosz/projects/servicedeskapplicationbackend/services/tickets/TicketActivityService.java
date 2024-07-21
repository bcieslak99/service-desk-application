package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.tickets;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.ticket.ChangeTicketStatusDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.groups.SupportGroup;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.*;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.ticket.TicketActivityException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.tickets.TicketActivitiesRepository;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;

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

    public void addUserChangeActivity(Ticket ticket, User who, User from, User to) throws TicketActivityException
    {
        if(ticket == null || who == null || from == null || to == null)
            throw new TicketActivityException(ACTIVITY_CREATION_ERROR_MESSAGE);

        TicketActivity activity = new TicketActivity();
        activity.setTicketActivityType(TicketActivityType.EDIT);
        activity.setDescription("Użytkownik " + who.getSurname() + " " + who.getName() + " (" + who.getMail() + ") zmienił użytkownika z " +
                from.getSurname() + " " + from.getName() + " (" + from.getMail() + ")" + " na " + to.getSurname() + " " + to.getName() + " (" + to.getMail() + ")");
        activity.setTicket(ticket);
        activity.setAnalyst(who);
        activity.setUserCanSee(true);
        activity.setActivityDate(LocalDateTime.now());
        this.TICKET_ACTIVITY_REPOSITORY.saveAndFlush(activity);
    }

    public void addReporterChangeActivity(Ticket ticket, User who, User from, User to) throws TicketActivityException
    {
        if(ticket == null || who == null || from == null || to == null)
            throw new TicketActivityException(ACTIVITY_CREATION_ERROR_MESSAGE);

        TicketActivity activity = new TicketActivity();
        activity.setTicketActivityType(TicketActivityType.EDIT);
        activity.setDescription("Użytkownik " + who.getSurname() + " " + who.getName() + " (" + who.getMail() + ") zmienił zgłaszającego z " +
                from.getSurname() + " " + from.getName() + " (" + from.getMail() + ")" + " na " + to.getSurname() + " " + to.getName() + " (" + to.getMail() + ")");
        activity.setTicket(ticket);
        activity.setAnalyst(who);
        activity.setUserCanSee(true);
        activity.setActivityDate(LocalDateTime.now());
        this.TICKET_ACTIVITY_REPOSITORY.saveAndFlush(activity);
    }

    public void addStatusChangeActivity(Ticket ticket, User who, TicketStatus from, ChangeTicketStatusDTO to) throws TicketActivityException
    {
        if(ticket == null || who == null || from == null || to == null)
            throw new TicketActivityException(ACTIVITY_CREATION_ERROR_MESSAGE);

        TicketActivity activity = new TicketActivity();
        activity.setTicketActivityType(TicketActivityType.CHANGE_STATUS);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Użytkownik ").append(who.getSurname()).append(" ").append(who.getName()).append(" (")
                .append(who.getMail()).append(") zmienił status zgłoszenia z \"")
                .append(TicketService.changeTicketStatusToPolish(from))
                .append("\" na \"").append(TicketService.changeTicketStatusToPolish(to.getNewTicketStatus())).append("\".");

        String comment = to.getComment();
        if(comment != null && !comment.trim().isEmpty())
            stringBuilder.append("\n").append("Komentarz: ").append(comment.trim());

        activity.setDescription(stringBuilder.toString());
        activity.setTicket(ticket);
        activity.setAnalyst(who);
        activity.setUserCanSee(false);
        activity.setActivityDate(LocalDateTime.now());
        this.TICKET_ACTIVITY_REPOSITORY.saveAndFlush(activity);
    }

    public void addComment(Ticket ticket, User who, String comment) throws TicketActivityException
    {
        if(ticket == null || who == null || comment == null || comment.trim().isEmpty())
            throw new TicketActivityException(ACTIVITY_CREATION_ERROR_MESSAGE);

        TicketActivity activity = new TicketActivity();
        activity.setTicketActivityType(TicketActivityType.COMMENT);
        activity.setDescription(comment.trim());
        activity.setTicket(ticket);
        activity.setAnalyst(who);
        activity.setUserCanSee(true);
        activity.setActivityDate(LocalDateTime.now());
        this.TICKET_ACTIVITY_REPOSITORY.saveAndFlush(activity);
    }

    public void addReminder(Ticket ticket, User who, String comment) throws TicketActivityException
    {
        if(ticket == null || who == null || comment == null || comment.trim().isEmpty())
            throw new TicketActivityException(ACTIVITY_CREATION_ERROR_MESSAGE);

        TicketActivity activity = new TicketActivity();
        activity.setTicketActivityType(TicketActivityType.COMPLAINT);
        activity.setDescription(comment.trim());
        activity.setTicket(ticket);
        activity.setAnalyst(who);
        activity.setUserCanSee(true);
        activity.setActivityDate(LocalDateTime.now());
        this.TICKET_ACTIVITY_REPOSITORY.saveAndFlush(activity);
    }

    public void addInternalNote(Ticket ticket, User who, String comment) throws TicketActivityException
    {
        if(ticket == null || who == null || comment == null || comment.trim().isEmpty())
            throw new TicketActivityException(ACTIVITY_CREATION_ERROR_MESSAGE);

        TicketActivity activity = new TicketActivity();
        activity.setTicketActivityType(TicketActivityType.INTERNAL_NOTE);
        activity.setDescription(comment.trim());
        activity.setTicket(ticket);
        activity.setAnalyst(who);
        activity.setUserCanSee(false);
        activity.setActivityDate(LocalDateTime.now());
        this.TICKET_ACTIVITY_REPOSITORY.saveAndFlush(activity);
    }

    public void addChangeCategoryActivity(Ticket ticket, User who, TicketCategory from, TicketCategory to) throws TicketActivityException
    {
        if(ticket == null || who == null || from == null | to == null)
            throw new TicketActivityException(ACTIVITY_CREATION_ERROR_MESSAGE);

        TicketActivity activity = new TicketActivity();
        activity.setTicketActivityType(TicketActivityType.EDIT);
        activity.setDescription("Użytkownik " + who.getSurname() + " " + who.getName() + " (" + who.getMail() + ") zmienił kategorię z \"" + from.getName() + "\" na \"" + to.getName() + "\".");
        activity.setTicket(ticket);
        activity.setAnalyst(who);
        activity.setUserCanSee(false);
        activity.setActivityDate(LocalDateTime.now());
        this.TICKET_ACTIVITY_REPOSITORY.saveAndFlush(activity);
    }

    public void addChangeDescriptionActivity(Ticket ticket, User who, String from, String to) throws TicketActivityException
    {
        if(ticket == null || who == null || from == null || to == null)
            throw new TicketActivityException(ACTIVITY_CREATION_ERROR_MESSAGE);

        TicketActivity activity = new TicketActivity();
        activity.setTicketActivityType(TicketActivityType.EDIT);
        activity.setDescription("Użytkownik " + who.getSurname() + " " + who.getName() + " (" + who.getMail() + ")" + " zmienił opis zgłoszenia z \"" + from + "\" na \"" + to + "\"");
        activity.setTicket(ticket);
        activity.setAnalyst(who);
        activity.setUserCanSee(false);
        activity.setActivityDate(LocalDateTime.now());
        this.TICKET_ACTIVITY_REPOSITORY.saveAndFlush(activity);
    }

    public void addChangeAnalystActivity(Ticket ticket, User who, User from, User to) throws TicketActivityException
    {
        if(ticket == null || who == null)
            throw new TicketActivityException(ACTIVITY_CREATION_ERROR_MESSAGE);

        TicketActivity activity = new TicketActivity();
        activity.setTicketActivityType(TicketActivityType.ASSIGN_INDIVIDUAL);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Użytkownik ").append(who.getSurname()).append(" ").append(who.getName()).append(" (").append(who.getMail()).append(") zmienił osobę realizującą");
        if(from != null) stringBuilder.append(" z ").append(from.getSurname()).append(" ").append(from.getName()).append(" (").append(from.getMail()).append(")");

        if(to == null) stringBuilder.append(" na nieprzypisane.");
        else stringBuilder.append(" na ").append(to.getSurname()).append(" ").append(to.getName()).append(" (").append(to.getMail()).append(").");

        activity.setDescription(stringBuilder.toString());
        activity.setTicket(ticket);
        activity.setAnalyst(who);
        activity.setUserCanSee(false);
        activity.setActivityDate(LocalDateTime.now());
        this.TICKET_ACTIVITY_REPOSITORY.saveAndFlush(activity);
    }

    public void addChangeGroupActivity(Ticket ticket, User who, SupportGroup from, SupportGroup to) throws TicketActivityException
    {
        if(ticket == null || who == null || from == null || to == null)
            throw new TicketActivityException(ACTIVITY_CREATION_ERROR_MESSAGE);

        TicketActivity activity = new TicketActivity();
        activity.setTicketActivityType(TicketActivityType.ASSIGN_GROUP);
        activity.setDescription("Użytkownik " + who.getSurname() + " " + who.getName() + " (" + who.getMail()
                + ") zmienił grupę realizującą z " + from.getName() + " na " + to.getName());
        activity.setTicket(ticket);
        activity.setAnalyst(who);
        activity.setUserCanSee(false);
        activity.setActivityDate(LocalDateTime.now());
        this.TICKET_ACTIVITY_REPOSITORY.saveAndFlush(activity);
    }
}
