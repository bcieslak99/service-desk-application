package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.controllers.api.v1;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.activities.TicketCommentDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.categories.CategoryIdDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.groups.GroupIdDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.responses.ResponseCode;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.responses.ResponseMessage;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.ticket.*;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.user.UserId;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketStatus;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketType;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.categories.CategoryIsDisabledException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.categories.CategoryNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.groups.GroupNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.system.EndpointNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.system.PermissionDeniedException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.ticket.*;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.users.UserNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.tickets.TicketService;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.user.UserService;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/ticket")
public class TicketController
{
    private final TicketService TICKET_SERVICE;
    private final UserService USER_SERVICE;

    private static final String INTERNAL_ERROR_MESSAGE = "Napotkano na nieoczekiwany błąd!";

    @PostMapping("/employee/create")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<?> createTicketAsEmployee(Principal principal, @Valid @RequestBody EmployeeTicketFormDTO ticket, BindingResult errors)
    {
        if(principal.getName() == null || errors.hasErrors())
            return ResponseEntity.badRequest().body(new ResponseMessage("Wskazane dane zawierają błędy!", ResponseCode.ERROR));
        
        try
        {
            UUID userId = USER_SERVICE.extractUserId(principal);
            if(userId == null)
                throw new UserNotFoundException("Nie odnaleziono Twoich danych!");

            AnalystTicketFormDTO form = AnalystTicketFormDTO.builder()
                    .description(ticket.getDescription())
                    .customer(ticket.getCustomer())
                    .reporter(userId)
                    .category(ticket.getCategory())
                    .build();

            return ResponseEntity.ok(this.TICKET_SERVICE.createTicket(userId, form).prepareDetailsForEmployee());
        }
        catch(UserNotFoundException | CategoryNotFoundException | CategoryIsDisabledException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage(INTERNAL_ERROR_MESSAGE, ResponseCode.ERROR));
        }
    }

    @GetMapping("/employee/list/{ticketType}/{ticketStatus}")
    @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'FIRST_LINE_ANALYST', 'SECOND_LINE_ANALYST')")
    ResponseEntity<?> getTickets(@PathVariable("ticketStatus") String ticketStatus, @PathVariable("ticketType") String ticketType, Principal principal)
    {
        try
        {
            return ResponseEntity.ok(this.TICKET_SERVICE.getUserTickets(principal, ticketType, ticketStatus));
        }
        catch(UserNotFoundException | EndpointNotFoundException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage(INTERNAL_ERROR_MESSAGE, ResponseCode.ERROR));
        }
    }

    @GetMapping("/employee/statistics")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<?> getEmployeeReportStatistics(Principal principal)
    {
        try
        {
            return ResponseEntity.ok(this.TICKET_SERVICE.getUserReportStatistics(this.USER_SERVICE.extractUserId(principal)));
        }
        catch (Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage(INTERNAL_ERROR_MESSAGE, ResponseCode.ERROR));
        }
    }

    @GetMapping("/details/{id}")
    @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'FIRST_LINE_ANALYST', 'SECOND_LINE_ANALYST')")
    public ResponseEntity<?> getTicketDetails(Principal principal, @PathVariable("id") UUID ticketId)
    {
        try
        {
            Optional<?> details = this.TICKET_SERVICE.getTicketDetails(principal, ticketId);
            if(details.isEmpty())
                throw new Exception();

            return ResponseEntity.ok(details.get());
        }
        catch(UserNotFoundException | TicketNotFoundException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(PermissionDeniedException exception)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage(INTERNAL_ERROR_MESSAGE, ResponseCode.ERROR));
        }
    }

    @GetMapping("/permissions/{id}")
    @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'FIRST_LINE_ANALYST', 'SECOND_LINE_ANALYST')")
    public ResponseEntity<?> getInformationAboutPermissions(@PathVariable("id") UUID ticketId, Principal principal)
    {
        try
        {
            return ResponseEntity.ok(this.TICKET_SERVICE.getInformationAboutPermissions(principal, ticketId));
        }
        catch(TicketNotFoundException | UserNotFoundException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage(INTERNAL_ERROR_MESSAGE, ResponseCode.ERROR));
        }
    }

    @PostMapping("/user/change/{id}")
    @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'FIRST_LINE_ANALYST', 'SECOND_LINE_ANALYST')")
    public ResponseEntity<ResponseMessage> changeUser(@PathVariable("id") UUID ticketId, @Valid @RequestBody UserId userId, BindingResult errors, Principal principal)
    {
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(new ResponseMessage("Identyfikator użytkownika jest nieprawidłowy!", ResponseCode.ERROR));

        try
        {
            this.TICKET_SERVICE.changeUser(principal, ticketId, userId.getUserId());
            return ResponseEntity.ok(new ResponseMessage("Użytkownik został zmieniony", ResponseCode.SUCCESS));
        }
        catch(UserNotFoundException | TicketNotFoundException | TicketActivityException | TicketIsClosedException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage(INTERNAL_ERROR_MESSAGE, ResponseCode.ERROR));
        }
    }

    @PostMapping("/reporter/change/{id}")
    @PreAuthorize("hasAnyAuthority('FIRST_LINE_ANALYST', 'SECOND_LINE_ANALYST')")
    public ResponseEntity<ResponseMessage> changeReporter(@PathVariable("id") UUID ticketId, @Valid @RequestBody UserId userId, BindingResult errors, Principal principal)
    {
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(new ResponseMessage("Identyfikator użytkownika jest nieprawidłowy!", ResponseCode.ERROR));

        try
        {
            this.TICKET_SERVICE.changeReporter(principal, ticketId, userId.getUserId());
            return ResponseEntity.ok(new ResponseMessage("Użytkownik został zmieniony", ResponseCode.SUCCESS));
        }
        catch(UserNotFoundException | TicketNotFoundException | TicketActivityException |
              TicketIsClosedException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage(INTERNAL_ERROR_MESSAGE, ResponseCode.ERROR));
        }
    }

    @PostMapping("/status/change/{id}")
    @PreAuthorize("hasAnyAuthority('FIRST_LINE_ANALYST', 'SECOND_LINE_ANALYST')")
    public ResponseEntity<ResponseMessage> changeStatus(@PathVariable("id") UUID ticketId, @Valid @RequestBody ChangeTicketStatusDTO ticketStatus, BindingResult errors, Principal principal)
    {
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(new ResponseMessage("Wskazano nieprawidłowy status!", ResponseCode.ERROR));

        try
        {
            this.TICKET_SERVICE.changeStatus(principal, ticketId, ticketStatus);
            return ResponseEntity.ok(new ResponseMessage("Status został zmieniony.", ResponseCode.SUCCESS));
        }
        catch(TicketNotFoundException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage(INTERNAL_ERROR_MESSAGE, ResponseCode.ERROR));
        }
    }

    @PostMapping("/activity/comment/add/{id}")
    @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'FIRST_LINE_ANALYST', 'SECOND_LINE_ANALYST')")
    public ResponseEntity<ResponseMessage> addComment(Principal principal, @PathVariable("id") UUID ticketId, @Valid @RequestBody TicketCommentDTO comment, BindingResult errors)
    {
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(new ResponseMessage("Treść komentarza jest nieprawidłowa!", ResponseCode.ERROR));

        try
        {
            this.TICKET_SERVICE.addComment(principal, ticketId, comment.getComment());
            return ResponseEntity.ok(new ResponseMessage("Komentarz został dodany.", ResponseCode.SUCCESS));
        }
        catch (UserNotFoundException | TicketNotFoundException | TicketActivityException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch (Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage(INTERNAL_ERROR_MESSAGE, ResponseCode.ERROR));
        }
    }

    @PostMapping("/activity/reminder/add/{id}")
    @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'FIRST_LINE_ANALYST')")
    public ResponseEntity<ResponseMessage> addReminder(Principal principal, @PathVariable("id") UUID ticketId, @Valid @RequestBody TicketCommentDTO comment, BindingResult errors)
    {
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(new ResponseMessage("Treść komentarza jest nieprawidłowa!", ResponseCode.ERROR));

        try
        {
            this.TICKET_SERVICE.addReminder(principal, ticketId, comment.getComment());
            return ResponseEntity.ok(new ResponseMessage("Monit został dodany.", ResponseCode.SUCCESS));
        }
        catch (UserNotFoundException | TicketNotFoundException | TicketActivityException | PermissionDeniedException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch (Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage(INTERNAL_ERROR_MESSAGE, ResponseCode.ERROR));
        }
    }

    @PostMapping("/activity/note/add/{id}")
    @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'FIRST_LINE_ANALYST', 'SECOND_LINE_ANALYST')")
    public ResponseEntity<ResponseMessage> addInternaNote(Principal principal, @PathVariable("id") UUID ticketId, @Valid @RequestBody TicketCommentDTO comment, BindingResult errors)
    {
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(new ResponseMessage("Treść komentarza jest nieprawidłowa!", ResponseCode.ERROR));

        try
        {
            this.TICKET_SERVICE.addInternalNote(principal, ticketId, comment.getComment());
            return ResponseEntity.ok(new ResponseMessage("Notatka został dodany.", ResponseCode.SUCCESS));
        }
        catch (UserNotFoundException | TicketNotFoundException | TicketActivityException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch (Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage(INTERNAL_ERROR_MESSAGE, ResponseCode.ERROR));
        }
    }

    @PatchMapping("/category/change/{id}")
    @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'FIRST_LINE_ANALYST')")
    public ResponseEntity<ResponseMessage> changeTicketCategory(@PathVariable("id") UUID ticketId, Principal principal, @Valid @RequestBody CategoryIdDTO categoryId, BindingResult errors)
    {
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(new ResponseMessage("Wskazano nieprawidłową kategorię!", ResponseCode.ERROR));

        try
        {
            this.TICKET_SERVICE.changeTicketCategory(principal, ticketId, categoryId.getCategoryId());
            return ResponseEntity.ok(new ResponseMessage("Notatka został dodany.", ResponseCode.SUCCESS));
        }
        catch (UserNotFoundException | TicketNotFoundException | TicketActivityException | CategoryNotFoundException | CategoryIsDisabledException | PermissionDeniedException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch (Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage(INTERNAL_ERROR_MESSAGE, ResponseCode.ERROR));
        }
    }

    @PatchMapping("/description/change/{id}")
    @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'FIRST_LINE_ANALYST', 'SECOND_LINE_ANALYST')")
    public ResponseEntity<ResponseMessage> changeTicketDescription(@PathVariable("id") UUID ticketId, Principal principal, @Valid @RequestBody TicketDescriptionDTO newDescription, BindingResult errors)
    {
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(new ResponseMessage("Nowy opis jest nieprawidłowy!", ResponseCode.ERROR));

        try
        {
            this.TICKET_SERVICE.changeTicketDescription(principal, ticketId, newDescription.getDescription());
            return ResponseEntity.ok(new ResponseMessage("Opis został zmieniony.", ResponseCode.SUCCESS));
        }
        catch(UserNotFoundException | TicketNotFoundException | TicketDescriptionException | TicketActivityException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage(INTERNAL_ERROR_MESSAGE, ResponseCode.ERROR));
        }
    }

    @PatchMapping("/assignee/analyst/change/{id}")
    @PreAuthorize("hasAnyAuthority('FIRST_LINE_ANALYST', 'SECOND_LINE_ANALYST')")
    public ResponseEntity<ResponseMessage> changeAssigneeAnalyst(@PathVariable("id") UUID ticketId, Principal principal, @Valid @RequestBody UserId userId, BindingResult errors)
    {
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(new ResponseMessage("Nie podano identyfikatora użytkownika", ResponseCode.ERROR));

        try
        {
            this.TICKET_SERVICE.changeAssigneePerson(ticketId, principal, userId.getUserId());
            return ResponseEntity.ok(new ResponseMessage("Osoba realizująca została zmieniona", ResponseCode.SUCCESS));
        }
        catch(UserNotFoundException | TicketNotFoundException | TicketIsClosedException | TicketActivityException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage(INTERNAL_ERROR_MESSAGE, ResponseCode.ERROR));
        }
    }

    @PatchMapping("/assignee/group/change/{id}")
    @PreAuthorize("hasAnyAuthority('FIRST_LINE_ANALYST', 'SECOND_LINE_ANALYST')")
    public ResponseEntity<ResponseMessage> changeAssigneeGroup(@PathVariable("id") UUID ticketId, Principal principal, @Valid @RequestBody GroupIdDTO groupId, BindingResult errors)
    {
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(new ResponseMessage("Nie podano grupy, której należy przekazać zgłoszenie!", ResponseCode.ERROR));

        try
        {
            this.TICKET_SERVICE.changeAssigneeGroup(ticketId, principal, groupId.getGroupId());
            return ResponseEntity.ok(new ResponseMessage("Zgłoszenie zostało przekazane.", ResponseCode.SUCCESS));
        }
        catch(TicketNotFoundException | TicketIsClosedException | TicketActivityException | UserNotFoundException | GroupNotFoundException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage(INTERNAL_ERROR_MESSAGE, ResponseCode.ERROR));
        }
    }

    @GetMapping("/assignee/group/list")
    @PreAuthorize("hasAnyAuthority('FIRST_LINE_ANALYST', 'SECOND_LINE_ANALYST')")
    ResponseEntity<?> getPendingTicketsOfMyGroup(Principal principal, @PathParam("ticketStatus") TicketStatus ticketStatus, @PathParam("ticketType") TicketType ticketType)
    {
        try
        {
            return ResponseEntity.ok(this.TICKET_SERVICE.getTicketsOfMyGroupByStatus(ticketStatus, ticketType, principal));
        }
        catch(UserNotFoundException | TicketStatusException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError().body(new ResponseMessage(INTERNAL_ERROR_MESSAGE, ResponseCode.ERROR));
        }
    }
}
