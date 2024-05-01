package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.controllers.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.tickets.TicketActivityService;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.tickets.TicketCategoryService;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.tickets.TicketService;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/ticket")
public class TicketController
{
    private final TicketService TICKET_SERVICE;
    private final TicketCategoryService TICKET_CATEGORY_SERVICE;
    private final TicketActivityService TICKET_ACTIVITY_SERVICE;
}
