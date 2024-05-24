package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.tickets;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.ticket.AnalystTicketFormDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.ticket.TicketDetailsForEmployeeDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.groups.SupportGroup;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.Ticket;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketCategory;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketStatus;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.categories.CategoryNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.users.UserNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.tickets.TicketRepository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.GroupsService;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.user.UserService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService
{
    private final TicketRepository TICKET_REPOSITORY;
    private final UserService USER_SERVICE;
    private final TicketCategoryService TICKET_CATEGORY_SERVICE;

    private static final String USER_NOT_FOUND_MESSAGE = "Nie rozpoznano Twojego konta!";
    private static final String CUSTOMER_NOT_FOUND_MESSAGE = "Nie odnaleziono wskazanego zgłaszającego!";
    private static final String REPORTER_NOT_FOUND_MESSAGE = "Nie odnaleziono wskazanego użytkownika!";
    private static final String CATEGORY_NOT_FOUND_MESSAGE = "Nie odnaleziono wskazanej kategorii!";

    private Ticket prepareTicket(String description, User reporter, User customer, TicketCategory category)
    {
        Ticket ticket = new Ticket();

        ticket.setTicketType(category.getTicketType());
        ticket.setDescription(description.trim());
        ticket.setCustomer(customer);
        ticket.setReporter(reporter);
        ticket.setStatus(TicketStatus.PENDING);
        ticket.setOpenDate(LocalDateTime.now());
        ticket.setCategory(category);
        ticket.setAssigneeGroup(category.getDefaultGroup());

        return ticket;
    }

    public Ticket createTicket(UUID userId, AnalystTicketFormDTO ticketData) throws Exception
    {
        Optional<User> userInDataBase = this.USER_SERVICE.getUserById(userId);
        if(userInDataBase.isEmpty())
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        Optional<User> reporterInDatabase = this.USER_SERVICE.getUserById(ticketData.getReporter());
        if(reporterInDatabase.isEmpty())
            throw new UserNotFoundException(REPORTER_NOT_FOUND_MESSAGE);

        Optional<User> customerInDatabase = this.USER_SERVICE.getUserById(ticketData.getCustomer());
        if(customerInDatabase.isEmpty())
            throw new UserNotFoundException(CUSTOMER_NOT_FOUND_MESSAGE);

        Optional<TicketCategory> categoryInDatabase = this.TICKET_CATEGORY_SERVICE.getCategoryAndGroupById(ticketData.getCategory());
        if(categoryInDatabase.isEmpty())
            throw new CategoryNotFoundException(CATEGORY_NOT_FOUND_MESSAGE);

        User reporter = reporterInDatabase.get();
        User customer = customerInDatabase.get();
        TicketCategory category = categoryInDatabase.get();

        Ticket ticket = prepareTicket(ticketData.getDescription(), reporter, customer, category);

        ticket = this.TICKET_REPOSITORY.saveAndFlush(ticket);

        return ticket;
    }
}
