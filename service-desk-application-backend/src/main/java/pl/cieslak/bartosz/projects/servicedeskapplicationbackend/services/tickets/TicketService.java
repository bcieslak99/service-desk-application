package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.tickets;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.auth.PermissionsToTicket;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.categories.CategoryDetailsForEmployeeDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.ticket.*;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.groups.GroupType;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.groups.SupportGroup;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.Ticket;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketCategory;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketStatus;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketType;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.categories.CategoryIsDisabledException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.categories.CategoryNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.groups.GroupNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.system.EndpointNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.system.PermissionDeniedException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.ticket.*;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.users.UserNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.tickets.TicketRepository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.GroupsService;
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
    private final GroupsService GROUP_SERVICE;

    private static final String USER_NOT_FOUND_MESSAGE = "Nie rozpoznano Twojego konta!";
    private static final String CUSTOMER_NOT_FOUND_MESSAGE = "Nie odnaleziono wskazanego użytkownika!";
    private static final String REPORTER_NOT_FOUND_MESSAGE = "Nie odnaleziono wskazanego zgłaszającego!";
    private static final String CATEGORY_NOT_FOUND_MESSAGE = "Nie odnaleziono wskazanej kategorii!";
    private static final String CATEGORY_IS_DISABLED_MESSAGE = "Ta kategoria jest dezaktywowana!";
    private static final String BAD_TICKET_STATUS_MESSAGE = "Wybrany status zgłoszenia nie istnieje!";
    private static final String BAD_TICKET_TYPE_MESSAGE = "Wybrany status zgłoszenia nie istnieje!";
    private static final String TICKET_NOT_FOUND_MESSAGE = "Nie odnaleziono wskazanego zgłoszenia!";
    private static final String PERMISSION_DENIED_MESSAGE = "Nie masz dostępu do tego zasobu!";
    private static final String TICKET_IS_CLOSED_MESSAGE = "Zgłoszenie jest zamknięte! Nie można go modyfikować!";
    private static final String TICKET_ACTIVITY_ERROR_MESSAGE = "Nie udało sie dodać aktywności!";
    private static final String TICKET_DESCRIPTION_MESSAGE = "Podano nieprawidłowy opis zgłoszenia!";
    private static final String GROUP_NOT_FOUND_MESSAGE = "Wskazana grupa nie została odnaleziona!";
    private static final String GROUP_IN_NOT_ACTIVE_MESSAGE = "Wskazana grupa jest nieaktywna!";

    public static TicketType extractTicketType(String ticketType)
    {
        if(ticketType == null) return null;

        return switch (ticketType.trim()) {
            case "service" -> TicketType.SERVICE_REQUEST;
            case "problem" -> TicketType.PROBLEM;
            case "incident" -> TicketType.INCIDENT;
            default -> null;
        };
    }

    public static TicketStatus extractTicketStatus(String ticketStatus)
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

    public static String changeTicketStatusToPolish(TicketStatus ticketStatus)
    {
        return switch (ticketStatus)
        {
            case PENDING -> "Oczekujące";
            case IN_PROGRESS -> "W trakcie";
            case ON_HOLD -> "Wstrzymane";
            case RESOLVED -> "Rozwiązane";
            case CLOSED -> "Zamknięte";
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

        User user = userInDataBase.get();

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

        this.TICKET_ACTIVITY_SERVICE.addCreationActivity(ticket, user);

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

    public boolean userHasAccessAsEmployeeToTicket(UUID userId, UUID ticketId) throws TicketNotFoundException
    {
        Optional<Ticket> ticketInDatabase = this.TICKET_REPOSITORY.getTicketGroupAndReporterByTicketId(ticketId);
        if(ticketInDatabase.isEmpty()) throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);
        Ticket ticket = ticketInDatabase.get();
        return ticket.getReporter().getId().equals(userId);
    }

    public boolean userHasAccessAsFirstLineAnalyst(UUID userId) throws UserNotFoundException
    {
        if(userId == null)
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        Optional<User> userInDatabase = this.USER_SERVICE.getUserById(userId);
        if(userInDatabase.isEmpty())
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        User user = userInDatabase.get();

        return user.getUserGroups().stream().anyMatch(element -> element.getGroupType().equals(GroupType.FIRST_LINE));
    }

    public boolean userHasAccessAsSecondLineAnalyst(UUID userId, UUID ticketId) throws UserNotFoundException, TicketNotFoundException
    {
        if(userId == null)
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        if(ticketId == null)
            throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);

        Optional<User> userInDatabase = this.USER_SERVICE.getUserById(userId);
        if(userInDatabase.isEmpty())
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        Optional<Ticket> ticketInDatabase = this.TICKET_REPOSITORY.getTicketGroupAndReporterByTicketId(ticketId);
        if(ticketInDatabase.isEmpty())
            throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);

        User user = userInDatabase.get();
        Ticket ticket = ticketInDatabase.get();

        return user.getUserGroups().stream().anyMatch(group -> group.getId().equals(ticket.getAssigneeGroup().getId()));
    }

    public Optional<?> getTicketDetails(Principal whoWants, UUID ticketId) throws UserNotFoundException, TicketNotFoundException, PermissionDeniedException
    {
        if(whoWants == null || whoWants.getName() == null || whoWants.getName().trim().isEmpty())
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        if(ticketId == null)
            throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);

        boolean hasAccessAsEmployee = userHasAccessAsEmployeeToTicket(this.USER_SERVICE.extractUserId(whoWants), ticketId);
        boolean hasAccessAsFirstLineAnalyst = userHasAccessAsFirstLineAnalyst(this.USER_SERVICE.extractUserId(whoWants));
        boolean hasAccessAsSecondLineAnalyst = userHasAccessAsSecondLineAnalyst(this.USER_SERVICE.extractUserId(whoWants), ticketId);

        Optional<Ticket> ticketInDatabase = this.TICKET_REPOSITORY.getTicketDetailsById(ticketId);
        if(ticketInDatabase.isEmpty())
            throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);

        Ticket ticket = ticketInDatabase.get();

        if(hasAccessAsFirstLineAnalyst || hasAccessAsSecondLineAnalyst) return Optional.of(ticket.prepareDetailsForAnalyst());
        else if(hasAccessAsEmployee) return Optional.of(ticket.prepareDetailsForEmployee());
        else throw new PermissionDeniedException(PERMISSION_DENIED_MESSAGE);
    }

    public PermissionsToTicket getInformationAboutPermissions(UUID whoWants, UUID ticketId) throws TicketNotFoundException, UserNotFoundException
    {
        PermissionsToTicket permissions = new PermissionsToTicket();
        permissions.setAccessAsEmployee(userHasAccessAsEmployeeToTicket(whoWants, ticketId));
        permissions.setAccessAsFirstLineAnalyst(userHasAccessAsFirstLineAnalyst(whoWants));
        permissions.setAccessAsSecondLineAnalyst(userHasAccessAsSecondLineAnalyst(whoWants, ticketId));
        return permissions;
    }

    public PermissionsToTicket getInformationAboutPermissions(Principal whoWants, UUID ticketId) throws TicketNotFoundException, UserNotFoundException
    {
        UUID userId = this.USER_SERVICE.extractUserId(whoWants);
        return getInformationAboutPermissions(userId, ticketId);
    }

    @Transactional
    public void changeUser(Principal whoWants, UUID ticketId, UUID newUserId) throws Exception
    {
        if(whoWants == null) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);
        if(ticketId == null) throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);
        if(newUserId == null) throw new UserNotFoundException(CUSTOMER_NOT_FOUND_MESSAGE);

        Optional<Ticket> ticketInDatabase = this.TICKET_REPOSITORY.getTicketDetailsById(ticketId);
        if(ticketInDatabase.isEmpty()) throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);
        Ticket ticket = ticketInDatabase.get();

        if(ticket.getStatus().equals(TicketStatus.CLOSED))
            throw new TicketIsClosedException(TICKET_IS_CLOSED_MESSAGE);

        Optional<User> userInDatabase = this.USER_SERVICE.getUserById(newUserId);
        if(userInDatabase.isEmpty()) throw new UserNotFoundException(CUSTOMER_NOT_FOUND_MESSAGE);
        User newUser = userInDatabase.get();

        PermissionsToTicket permissions = getInformationAboutPermissions(whoWants, ticketId);
        if(!permissions.isAccessAsFirstLineAnalyst() && !permissions.isAccessAsSecondLineAnalyst() && !permissions.isAccessAsEmployee())
            throw new PermissionDeniedException(PERMISSION_DENIED_MESSAGE);

        Optional<User> analystInDatabase = this.USER_SERVICE.getUserById(this.USER_SERVICE.extractUserId(whoWants));
        if(analystInDatabase.isEmpty()) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);
        User analyst = analystInDatabase.get();

        User lastUser = ticket.getCustomer();
        ticket.setCustomer(newUser);

        this.TICKET_REPOSITORY.saveAndFlush(ticket);
        this.TICKET_ACTIVITY_SERVICE.addUserChangeActivity(ticket, analyst, lastUser, newUser);
    }

    @Transactional
    public void changeReporter(Principal whoWants, UUID ticketId, UUID newReporterId) throws Exception
    {
        if(whoWants == null) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);
        if(ticketId == null) throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);
        if(newReporterId == null) throw new UserNotFoundException(CUSTOMER_NOT_FOUND_MESSAGE);

        Optional<Ticket> ticketInDatabase = this.TICKET_REPOSITORY.getTicketDetailsById(ticketId);
        if(ticketInDatabase.isEmpty()) throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);
        Ticket ticket = ticketInDatabase.get();

        if(ticket.getStatus().equals(TicketStatus.CLOSED))
            throw new TicketIsClosedException(TICKET_IS_CLOSED_MESSAGE);

        Optional<User> userInDatabase = this.USER_SERVICE.getUserById(newReporterId);
        if(userInDatabase.isEmpty()) throw new UserNotFoundException(CUSTOMER_NOT_FOUND_MESSAGE);
        User newReporter = userInDatabase.get();

        PermissionsToTicket permissions = getInformationAboutPermissions(whoWants, ticketId);
        if(!permissions.isAccessAsFirstLineAnalyst() && !permissions.isAccessAsSecondLineAnalyst())
            throw new PermissionDeniedException(PERMISSION_DENIED_MESSAGE);

        Optional<User> analystInDatabase = this.USER_SERVICE.getUserById(this.USER_SERVICE.extractUserId(whoWants));
        if(analystInDatabase.isEmpty()) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);
        User analyst = analystInDatabase.get();

        User lastReporter = ticket.getCustomer();
        ticket.setReporter(newReporter);

        this.TICKET_REPOSITORY.saveAndFlush(ticket);
        this.TICKET_ACTIVITY_SERVICE.addReporterChangeActivity(ticket, analyst, lastReporter, newReporter);
    }

    @Transactional
    public void changeStatus(Principal whoWants, UUID ticketId, ChangeTicketStatusDTO newStatus) throws Exception
    {
        if(ticketId == null) throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);
        if(newStatus == null) throw new TicketStatusException(BAD_TICKET_STATUS_MESSAGE);

        Optional<Ticket> ticketInDatabase = this.TICKET_REPOSITORY.getTicketDetailsById(ticketId);
        if(ticketInDatabase.isEmpty()) throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);
        Ticket ticket = ticketInDatabase.get();

        if(ticket.getStatus().equals(TicketStatus.CLOSED))
            throw new TicketIsClosedException(TICKET_IS_CLOSED_MESSAGE);

        PermissionsToTicket permissions = getInformationAboutPermissions(whoWants, ticketId);
        if(!permissions.isAccessAsFirstLineAnalyst() && !permissions.isAccessAsSecondLineAnalyst())
            throw new PermissionDeniedException(PERMISSION_DENIED_MESSAGE);

        Optional<User> analystInDatabase = this.USER_SERVICE.getUserById(this.USER_SERVICE.extractUserId(whoWants));
        if(analystInDatabase.isEmpty()) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);
        User analyst = analystInDatabase.get();

        if(ticket.getAssigneeAnalyst() == null && !newStatus.getNewTicketStatus().equals(TicketStatus.PENDING))
            ticket.setAssigneeAnalyst(analyst);
        else if(ticket.getAssigneeAnalyst() != null && newStatus.getNewTicketStatus().equals(TicketStatus.PENDING))
            ticket.setAssigneeAnalyst(null);

        if(newStatus.getNewTicketStatus().equals(TicketStatus.RESOLVED))
            ticket.setResolveDate(LocalDateTime.now());

        if(!newStatus.getNewTicketStatus().equals(TicketStatus.RESOLVED) && !newStatus.getNewTicketStatus().equals(TicketStatus.CLOSED))
            ticket.setResolveDate(null);

        TicketStatus lastStatus = ticket.getStatus();
        ticket.setStatus(newStatus.getNewTicketStatus());

        this.TICKET_REPOSITORY.saveAndFlush(ticket);
        this.TICKET_ACTIVITY_SERVICE.addStatusChangeActivity(ticket, analyst, lastStatus, newStatus);
    }

    @Transactional
    public void addComment(Principal who, UUID ticketId, String comment) throws Exception
    {
        if(who == null || who.getName() == null || who.getName().trim().isEmpty())
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        if(ticketId == null) throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);
        if(comment == null || comment.trim().isEmpty()) throw new TicketActivityException(TICKET_ACTIVITY_ERROR_MESSAGE);

        Optional<User> userInDatabase = this.USER_SERVICE.getUserById(this.USER_SERVICE.extractUserId(who));
        if(userInDatabase.isEmpty()) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);
        User user = userInDatabase.get();

        Optional<Ticket> ticketInDatabase = this.TICKET_REPOSITORY.getTicketDetailsById(ticketId);
        if(ticketInDatabase.isEmpty()) throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);
        Ticket ticket = ticketInDatabase.get();

        this.TICKET_ACTIVITY_SERVICE.addComment(ticket, user, comment);
    }

    @Transactional
    public void addReminder(Principal who, UUID ticketId, String comment) throws Exception
    {
        if(who == null || who.getName() == null || who.getName().trim().isEmpty())
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        if(ticketId == null) throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);
        if(comment == null || comment.trim().isEmpty()) throw new TicketActivityException(TICKET_ACTIVITY_ERROR_MESSAGE);

        Optional<User> userInDatabase = this.USER_SERVICE.getUserById(this.USER_SERVICE.extractUserId(who));
        if(userInDatabase.isEmpty()) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);
        User user = userInDatabase.get();

        Optional<Ticket> ticketInDatabase = this.TICKET_REPOSITORY.getTicketDetailsById(ticketId);
        if(ticketInDatabase.isEmpty()) throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);
        Ticket ticket = ticketInDatabase.get();

        PermissionsToTicket permissions = getInformationAboutPermissions(who, ticketId);
        if(!(permissions.isAccessAsEmployee() || permissions.isAccessAsFirstLineAnalyst()))
            throw new PermissionDeniedException(PERMISSION_DENIED_MESSAGE);

        this.TICKET_ACTIVITY_SERVICE.addReminder(ticket, user, comment);
    }

    @Transactional
    public void addInternalNote(Principal who, UUID ticketId, String comment) throws Exception
    {
        if(who == null || who.getName() == null || who.getName().trim().isEmpty())
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        if(ticketId == null) throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);
        if(comment == null || comment.trim().isEmpty()) throw new TicketActivityException(TICKET_ACTIVITY_ERROR_MESSAGE);

        Optional<User> userInDatabase = this.USER_SERVICE.getUserById(this.USER_SERVICE.extractUserId(who));
        if(userInDatabase.isEmpty()) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);
        User user = userInDatabase.get();

        Optional<Ticket> ticketInDatabase = this.TICKET_REPOSITORY.getTicketDetailsById(ticketId);
        if(ticketInDatabase.isEmpty()) throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);
        Ticket ticket = ticketInDatabase.get();

        this.TICKET_ACTIVITY_SERVICE.addInternalNote(ticket, user, comment);
    }

    @Transactional
    public void changeTicketCategory(Principal who, UUID ticketId, UUID categoryId) throws Exception
    {
        if(who == null || who.getName() == null || who.getName().trim().isEmpty())
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        if(ticketId == null) throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);
        if(categoryId == null) throw new CategoryNotFoundException(CATEGORY_NOT_FOUND_MESSAGE);

        Optional<User> userInDatabase = this.USER_SERVICE.getUserById(this.USER_SERVICE.extractUserId(who));
        if(userInDatabase.isEmpty()) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);
        User user = userInDatabase.get();

        Optional<Ticket> ticketInDatabase = this.TICKET_REPOSITORY.getTicketDetailsById(ticketId);
        if(ticketInDatabase.isEmpty()) throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);
        Ticket ticket = ticketInDatabase.get();

        Optional<TicketCategory> categoryInDatabase = this.TICKET_CATEGORY_SERVICE.getCategoryById(categoryId);
        if(categoryInDatabase.isEmpty()) throw new CategoryNotFoundException(CATEGORY_NOT_FOUND_MESSAGE);
        TicketCategory category = categoryInDatabase.get();
        if(!category.isCategoryIsActive()) throw new CategoryIsDisabledException(CATEGORY_IS_DISABLED_MESSAGE);

        PermissionsToTicket permissions = getInformationAboutPermissions(user.getId(), ticketId);
        if(!(permissions.isAccessAsFirstLineAnalyst() || permissions.isAccessAsSecondLineAnalyst()))
            throw new PermissionDeniedException(PERMISSION_DENIED_MESSAGE);


        TicketCategory oldCategory = ticket.getCategory();
        ticket.setCategory(category);

        this.TICKET_REPOSITORY.saveAndFlush(ticket);
        this.TICKET_ACTIVITY_SERVICE.addChangeCategoryActivity(ticket, user, oldCategory, category);
    }

    @Transactional
    public void changeTicketDescription(Principal who, UUID ticketId, String description) throws Exception
    {
        if(who == null || who.getName() == null || who.getName().trim().isEmpty())
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        if(ticketId == null) throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);
        if(description == null || description.trim().isEmpty()) throw new TicketDescriptionException(TICKET_DESCRIPTION_MESSAGE);

        Optional<User> userInDatabase = this.USER_SERVICE.getUserById(this.USER_SERVICE.extractUserId(who));
        if(userInDatabase.isEmpty()) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);
        User user = userInDatabase.get();

        Optional<Ticket> ticketInDatabase = this.TICKET_REPOSITORY.getTicketDetailsById(ticketId);
        if(ticketInDatabase.isEmpty()) throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);
        Ticket ticket = ticketInDatabase.get();

        String oldDescription = ticket.getDescription();
        ticket.setDescription(description);

        this.TICKET_REPOSITORY.saveAndFlush(ticket);
        this.TICKET_ACTIVITY_SERVICE.addChangeDescriptionActivity(ticket, user, oldDescription, description);
    }

    @Transactional
    public void changeAssigneePerson(UUID ticketId, Principal who, UUID userId) throws Exception
    {
        if(who == null || who.getName() == null || who.getName().trim().isEmpty())
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        if(ticketId == null) throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);
        if(userId == null) throw new UserNotFoundException(CUSTOMER_NOT_FOUND_MESSAGE);

        Optional<User> userInDatabase = this.USER_SERVICE.getUserById(this.USER_SERVICE.extractUserId(who));
        if(userInDatabase.isEmpty()) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);
        User user = userInDatabase.get();

        Optional<User> analystInDatabase = this.USER_SERVICE.getUserById(userId);
        if(analystInDatabase.isEmpty()) throw new UserNotFoundException(CUSTOMER_NOT_FOUND_MESSAGE);
        User analyst = analystInDatabase.get();

        Optional<Ticket> ticketInDatabase = this.TICKET_REPOSITORY.getTicketDetailsById(ticketId);
        if(ticketInDatabase.isEmpty()) throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);
        Ticket ticket = ticketInDatabase.get();

        if(ticket.getStatus().equals(TicketStatus.CLOSED))
            throw new TicketIsClosedException(TICKET_IS_CLOSED_MESSAGE);

        Optional<SupportGroup> groupInDatabase = this.GROUP_SERVICE.getGroupAndMembersById(ticket.getAssigneeGroup().getId());
        if(groupInDatabase.isEmpty()) throw new Exception("Błąd - nieprzypisana grupa do zgłoszenia!");
        SupportGroup group = groupInDatabase.get();

        List<User> members = group.getGroupMembers();
        if(members.stream().noneMatch(member -> member.getId().equals(user.getId())))
            throw new UserNotFoundException(CUSTOMER_NOT_FOUND_MESSAGE);

        User oldUser = ticket.getAssigneeAnalyst();

        ticket.setAssigneeAnalyst(analyst);
        if(ticket.getStatus().equals(TicketStatus.PENDING))
        {
            TicketStatus oldStatus = ticket.getStatus();
            ticket.setStatus(TicketStatus.IN_PROGRESS);
            this.TICKET_REPOSITORY.saveAndFlush(ticket);
            this.TICKET_ACTIVITY_SERVICE.addStatusChangeActivity(ticket, user, oldStatus, new ChangeTicketStatusDTO(TicketStatus.IN_PROGRESS, "Rozpoczęcie realizacji"));
        }
        this.TICKET_REPOSITORY.saveAndFlush(ticket);
        this.TICKET_ACTIVITY_SERVICE.addChangeAnalystActivity(ticket, user, oldUser, analyst);
    }

    @Transactional
    public void changeAssigneeGroup(UUID ticketId, Principal who, UUID groupId) throws Exception
    {
        Optional<Ticket> ticketInDatabase = this.TICKET_REPOSITORY.getTicketDetailsById(ticketId);
        if(ticketInDatabase.isEmpty()) throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);
        Ticket ticket = ticketInDatabase.get();

        if(ticket.getStatus().equals(TicketStatus.CLOSED))
            throw new TicketIsClosedException(TICKET_IS_CLOSED_MESSAGE);

        Optional<User> userInDatabase = this.USER_SERVICE.getUserById(this.USER_SERVICE.extractUserId(who));
        if(userInDatabase.isEmpty()) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);
        User user = userInDatabase.get();

        Optional<SupportGroup> groupInDatabase = this.GROUP_SERVICE.getGroupById(groupId);
        if(groupInDatabase.isEmpty()) throw new GroupNotFoundException(GROUP_NOT_FOUND_MESSAGE);
        SupportGroup group = groupInDatabase.get();
        if(!group.isGroupActive()) throw new GroupNotFoundException(GROUP_IN_NOT_ACTIVE_MESSAGE);

        SupportGroup oldGroup = ticket.getAssigneeGroup();

        ticket.setAssigneeGroup(group);
        ticket.setAssigneeAnalyst(null);
        this.TICKET_REPOSITORY.saveAndFlush(ticket);
        this.TICKET_ACTIVITY_SERVICE.addChangeGroupActivity(ticket, user, oldGroup, group);
    }

    public List<TicketDetailsForAnalystDTO> getTicketsOfMyGroupByStatusAndType(TicketStatus ticketStatus, TicketType ticketType, Principal principal) throws Exception
    {
        if(ticketStatus == null) throw new TicketStatusException(BAD_TICKET_STATUS_MESSAGE);
        if(ticketType == null) throw new TicketStatusException(BAD_TICKET_TYPE_MESSAGE);
        if(principal == null || principal.getName() == null || principal.getName().trim().isEmpty())
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        UUID userId = this.USER_SERVICE.extractUserId(principal);
        if(userId == null) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        List<Ticket> ticketsInDatabase = this.TICKET_REPOSITORY.getTicketOfMyGroupsByStatusAndTicketType(ticketStatus, ticketType, userId);

        ArrayList<TicketDetailsForAnalystDTO> tickets = new ArrayList<>();
        if(ticketsInDatabase.size() > 0) tickets.ensureCapacity(ticketsInDatabase.size());

        ticketsInDatabase.forEach(ticket -> {
            TicketDetailsForAnalystDTO details = ticket.prepareDetailsForAnalyst();
            if(details != null) tickets.add(details);
        });

        return tickets;
    }

    public List<TicketDetailsForAnalystDTO> getUserTicketsByStatusAndType(TicketStatus ticketStatus, TicketType ticketType, Principal principal) throws Exception
    {
        if(ticketStatus == null) throw new TicketStatusException(BAD_TICKET_STATUS_MESSAGE);
        if(ticketType == null) throw new TicketStatusException(BAD_TICKET_TYPE_MESSAGE);
        if(principal == null || principal.getName() == null || principal.getName().trim().isEmpty())
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        UUID userId = this.USER_SERVICE.extractUserId(principal);
        if(userId == null) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        List<Ticket> ticketsInDatabase = this.TICKET_REPOSITORY.getUserTicketsByStatusAndType(ticketStatus, ticketType, userId);

        ArrayList<TicketDetailsForAnalystDTO> tickets = new ArrayList<>();
        if(ticketsInDatabase.size() > 0) tickets.ensureCapacity(ticketsInDatabase.size());

        ticketsInDatabase.forEach(ticket -> {
            TicketDetailsForAnalystDTO details = ticket.prepareDetailsForAnalyst();
            if(details != null) tickets.add(details);
        });

        return tickets;
    }

    public boolean ticketExists(UUID ticketId)
    {
        if(ticketId == null) return false;
        return this.TICKET_REPOSITORY.getTicketDetailsById(ticketId).isPresent();
    }
}
