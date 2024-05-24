package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.tickets;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketCategory;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
interface TicketCategoriesSQLRepository extends JpaRepository<TicketCategory, UUID>, TicketCategoriesRepository
{
    @Query("select c from TicketCategory as c left join fetch c.defaultGroup as dg left join fetch dg.groupManager order by c.categoryIsActive desc, c.ticketType, c.name")
    List<TicketCategory> gettAllCategories();

    @Query("select c from TicketCategory as c left join fetch c.defaultGroup as dg where c.ticketType = :ticketType order by c.name")
    List<TicketCategory> getAllCategoriesByTicketType(@Param("ticketType") TicketType ticketType);

    @Query("select c from TicketCategory as c left join fetch c.defaultGroup as dg where c.categoryIsActive = true and c.ticketType = :ticketType and dg.groupActive = true order by  c.name")
    List<TicketCategory> getActiveCategoriesByTicketType(@Param("ticketType") TicketType ticketType);

    Optional<TicketCategory> getTicketCategoryByName(String name);

    @Query("select c from TicketCategory as c left join fetch c.defaultGroup as dg where c.id = :id")
    Optional<TicketCategory> getCategoryAndGroupById(@Param("id") UUID categoryId);
}
