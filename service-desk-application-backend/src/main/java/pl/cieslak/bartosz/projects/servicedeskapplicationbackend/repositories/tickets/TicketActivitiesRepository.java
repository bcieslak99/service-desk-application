package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.tickets;

import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketActivity;

public interface TicketActivitiesRepository
{
    TicketActivity saveAndFlush(TicketActivity activity);
}
