package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.tickets;

import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketCategory;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketCategoriesRepository
{
    TicketCategory saveAndFlush(TicketCategory category);
    List<TicketCategory> gettAllCategories();
    List<TicketCategory> getAllCategoriesByTicketType(TicketType ticketType);

    List<TicketCategory> getActiveCategoriesByTicketType(TicketType ticketType);
    Optional<TicketCategory> getTicketCategoryByName(String name);
    Optional<TicketCategory> findById(UUID categoryId);
    Optional<TicketCategory> getCategoryAndGroupById(UUID categoryId);
}
