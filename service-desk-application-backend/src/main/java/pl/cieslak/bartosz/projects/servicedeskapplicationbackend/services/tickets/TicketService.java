package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.tickets;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.ticket.AnalystTicketFormDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.ticket.TicketStatusStatistics;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.ticket.TicketTypeStatisticsDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.Ticket;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketCategory;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketStatus;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketType;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.categories.CategoryIsDisabledException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.categories.CategoryNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.users.UserNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.tickets.TicketRepository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.user.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService
{
    private final TicketRepository TICKET_REPOSITORY;
    private final UserService USER_SERVICE;
    private final TicketCategoryService TICKET_CATEGORY_SERVICE;
    private final TicketActivityService TICKET_ACTIVITY_SERVICE;

    private static final String USER_NOT_FOUND_MESSAGE = "Nie rozpoznano Twojego konta!";
    private static final String CUSTOMER_NOT_FOUND_MESSAGE = "Nie odnaleziono wskazanego zgłaszającego!";
    private static final String REPORTER_NOT_FOUND_MESSAGE = "Nie odnaleziono wskazanego użytkownika!";
    private static final String CATEGORY_NOT_FOUND_MESSAGE = "Nie odnaleziono wskazanej kategorii!";
    private static final String CATEGORY_IS_DISABLED_MESSAGE = "Ta kategoria jest dezaktywowana!";

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

    @Transactional
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

        if(!category.isCategoryIsActive())
            throw new CategoryIsDisabledException(CATEGORY_IS_DISABLED_MESSAGE);

        Ticket ticket = prepareTicket(ticketData.getDescription(), reporter, customer, category);

        ticket = this.TICKET_REPOSITORY.saveAndFlush(ticket);

        this.TICKET_ACTIVITY_SERVICE.addCreationActivity(ticket, reporter);

        return ticket;
    }

    public TicketStatusStatistics getTicketStatusStatistics(List<Ticket> tickets)
    {
        if(tickets == null) return null;
        TicketStatusStatistics statistics = new TicketStatusStatistics();
        statistics.setPending(tickets.stream().filter(ticket -> ticket.getStatus().equals(TicketStatus.PENDING)).count());
        statistics.setInProgress(tickets.stream().filter(ticket -> ticket.getStatus().equals(TicketStatus.IN_PROGRESS)).count());
        statistics.setOnHold(tickets.stream().filter(ticket -> ticket.getStatus().equals(TicketStatus.ON_HOLD)).count());
        statistics.setResolved(tickets.stream().filter(ticket -> ticket.getStatus().equals(TicketStatus.RESOLVED)).count());
        return statistics;
    }

    public TicketTypeStatisticsDTO getUserReportStatistics(UUID userId)
    {
        List<Ticket> tickets = this.TICKET_REPOSITORY.getUserTicketsByUserIdAndTicketStatus(userId,
                Arrays.asList(TicketStatus.PENDING, TicketStatus.IN_PROGRESS, TicketStatus.ON_HOLD, TicketStatus.RESOLVED));

        TicketTypeStatisticsDTO statistics = new TicketTypeStatisticsDTO();

        statistics.setIncidents(getTicketStatusStatistics(tickets.stream()
                .filter(ticket -> ticket.getTicketType().equals(TicketType.INCIDENT)).collect(Collectors.toList())));

        statistics.setServiceRequests(getTicketStatusStatistics(tickets.stream()
                .filter(ticket -> ticket.getTicketType().equals(TicketType.SERVICE_REQUEST)).collect(Collectors.toList())));

        return statistics;
    }
}
