package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.tickets;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.auth.UserEntityDetails;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.categories.CategoryDetailsForEmployeeDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.ticket.*;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.Ticket;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketCategory;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketStatus;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketType;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.categories.CategoryIsDisabledException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.categories.CategoryNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.system.EndpointNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.users.UserNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.tickets.TicketRepository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.user.UserService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
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
    private static final String BAD_TICKET_STATUS_MESSAGE = "Wybrany status zgłoszenia nie istnieje!";
    private static final String BAD_TICKET_TYPE_MESSAGE = "Wybrany status zgłoszenia nie istnieje!";

    public TicketType extractTicketType(String ticketType)
    {
        if(ticketType == null) return null;

        return switch (ticketType.trim()) {
            case "service" -> TicketType.SERVICE_REQUEST;
            case "problem" -> TicketType.PROBLEM;
            case "incident" -> TicketType.INCIDENT;
            default -> null;
        };
    }

    public TicketStatus extractTicketStatus(String ticketStatus)
    {
        if(ticketStatus == null) return null;

        return switch(ticketStatus.trim()) {
            case "pending" -> TicketStatus.PENDING;
            case "progress" -> TicketStatus.IN_PROGRESS;
            case "hold" -> TicketStatus.ON_HOLD;
            case "resolved" -> TicketStatus.RESOLVED;
            case "closed" -> TicketStatus.CLOSED;
            default -> null;
        };
    }

    public TicketDetailsForEmployeeDTO prepareTicketDetailsForEmployee(Ticket ticket)
    {
        return TicketDetailsForEmployeeDTO
                .builder()
                .id(ticket.getId())
                .ticketType(ticket.getTicketType())
                .description(ticket.getDescription())
                .customer(this.USER_SERVICE.prepareUserDetails(ticket.getCustomer()))
                .reporter(this.USER_SERVICE.prepareUserDetails(ticket.getReporter()))
                .status(ticket.getStatus())
                .openDate(ticket.getOpenDate())
                .resolveDate(ticket.getResolveDate())
                .closeDate(ticket.getCloseDate())
                .category(
                        CategoryDetailsForEmployeeDTO
                        .builder()
                        .id(ticket.getCategory().getId())
                        .name(ticket.getCategory().getName())
                        .description(ticket.getCategory().getDescription())
                        .build()
                ).build();
    }

    public TicketAsListElementDTO prepareTicketAsListElement(Ticket ticket)
    {
        if(ticket == null) return null;
        return TicketAsListElementDTO
                .builder()
                .id(ticket.getId())
                .ticketStatus(ticket.getStatus())
                .ticketType(ticket.getTicketType())
                .description(ticket.getDescription())
                .customer(this.USER_SERVICE.prepareUserDetails(ticket.getCustomer()))
                .reporter(this.USER_SERVICE.prepareUserDetails(ticket.getReporter()))
                .build();
    }

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

    public List<TicketDetailsForEmployeeDTO> getUserTickets(Principal principal, String ticketType, String ticketStatus) throws Exception
    {
        UUID userId = this.USER_SERVICE.extractUserId(principal);
        if(userId == null)
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        TicketType type = extractTicketType(ticketType);
        TicketStatus status = extractTicketStatus(ticketStatus);

        if(type == null || status == null)
            throw new EndpointNotFoundException(type == null ? BAD_TICKET_TYPE_MESSAGE : BAD_TICKET_STATUS_MESSAGE);

        List<Ticket> ticketsInDatabase = this.TICKET_REPOSITORY.getTicketsByTypeAndStatus(userId, type, status);
        List<TicketDetailsForEmployeeDTO> tickets = new ArrayList<>();

        ticketsInDatabase.forEach(ticket -> tickets.add(prepareTicketDetailsForEmployee(ticket)));

        return tickets.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }
}
