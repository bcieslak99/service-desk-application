package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.tickets;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.tickets.TicketCategoriesRepository;

@Service
@RequiredArgsConstructor
public class TicketCategoryService
{
    private final TicketCategoriesRepository TICKET_CATEGORY_REPOSITORY;
}
