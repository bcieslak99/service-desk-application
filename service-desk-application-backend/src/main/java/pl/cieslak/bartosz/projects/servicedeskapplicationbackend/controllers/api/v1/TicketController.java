package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.controllers.api.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.responses.ResponseCode;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.responses.ResponseMessage;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.ticket.AnalystTicketFormDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.ticket.EmployeeTicketFormDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.categories.CategoryIsDisabledException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.categories.CategoryNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.system.EndpointNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.system.PermissionDeniedException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.ticket.TicketNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.users.UserNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.tickets.TicketActivityService;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.tickets.TicketCategoryService;
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
    private final TicketCategoryService TICKET_CATEGORY_SERVICE;
    private final TicketActivityService TICKET_ACTIVITY_SERVICE;
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
}
