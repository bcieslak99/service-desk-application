package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.tickets;

import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketCategory;

public interface TicketCategoriesRepository
{
    TicketCategory saveAndFlush(TicketCategory category);
}
