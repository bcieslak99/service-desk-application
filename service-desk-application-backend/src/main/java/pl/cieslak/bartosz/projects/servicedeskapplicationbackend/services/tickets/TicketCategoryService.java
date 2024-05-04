package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.tickets;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.categories.CategoryDetailsDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.categories.NewTicketCategoryDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.responses.ResponseCode;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.responses.ResponseMessage;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.groups.SupportGroup;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketCategory;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketType;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.categories.CategoryDataEmptyException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.categories.CategoryExistsException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.categories.CategoryNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.groups.GroupNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.tickets.TicketCategoriesRepository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.GroupsService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketCategoryService
{
    private final TicketCategoriesRepository TICKET_CATEGORY_REPOSITORY;
    private final GroupsService GROUP_SERVICE;

    public CategoryDetailsDTO prepareCategoryDetails(TicketCategory category)
    {
        if(category == null) return null;

        return CategoryDetailsDTO.builder()
                .categoryId(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .categoryActive(category.isCategoryIsActive())
                .ticketType(category.getTicketType())
                .defaultGroup(category.getDefaultGroup().getId())
                .build();
    }

    public List<CategoryDetailsDTO> getAllCategories()
    {
        List<TicketCategory> categoriesFromDatabase = this.TICKET_CATEGORY_REPOSITORY.gettAllCategories();
        List<CategoryDetailsDTO> categories = new ArrayList<>();

        for(TicketCategory category : categoriesFromDatabase)
            categories.add(prepareCategoryDetails(category));

        categories = categories.stream().filter(Objects::nonNull).collect(Collectors.toList());

        return categories;
    }

    public List<CategoryDetailsDTO> getAllCategoriesByTicketType(TicketType ticketType)
    {
        List<TicketCategory> categoriesFromDatabase = this.TICKET_CATEGORY_REPOSITORY.getAllCategoriesByTicketType(ticketType);
        List<CategoryDetailsDTO> categories = new ArrayList<>();

        for(TicketCategory category : categoriesFromDatabase)
            categories.add(prepareCategoryDetails(category));

        categories = categories.stream().filter(Objects::nonNull).collect(Collectors.toList());

        return categories;
    }

    public List<CategoryDetailsDTO> getActiveCategoriesByTicketType(TicketType ticketType)
    {
        List<TicketCategory> categoriesFromDatabase = this.TICKET_CATEGORY_REPOSITORY.getActiveCategoriesByTicketType(ticketType);
        List<CategoryDetailsDTO> categories = new ArrayList<>();

        for(TicketCategory category : categoriesFromDatabase)
            categories.add(prepareCategoryDetails(category));

        categories = categories.stream().filter(Objects::nonNull).collect(Collectors.toList());

        return categories;
    }

    public ResponseEntity<ResponseMessage> createNewCategory(NewTicketCategoryDTO categoryData)
    {
        try
        {
            if(categoryData == null) throw new CategoryDataEmptyException("Dane nowej kategorii nie mogą być puste!");

            Optional<TicketCategory> categoryInDatabase = this.TICKET_CATEGORY_REPOSITORY
                    .getTicketCategoryByName(categoryData.getName().trim());

            if(categoryInDatabase.isPresent())
                throw new CategoryExistsException("Kategoria z taką nazwą już istnieje!");

            Optional<SupportGroup> groupInDatabase = this.GROUP_SERVICE.getGroupById(categoryData.getDefaultGroup());
            if(groupInDatabase.isEmpty()) throw new GroupNotFoundException("Nie odnaleziono grupy, która ma być domyślną dla tej kategorii!");

            SupportGroup group = groupInDatabase.get();

            TicketCategory category = new TicketCategory();
            category.setName(categoryData.getName());
            category.setDescription(categoryData.getDescription());
            category.setCategoryIsActive(true);
            category.setTicketType(categoryData.getTicketType());
            category.setDefaultGroup(group);

            this.TICKET_CATEGORY_REPOSITORY.saveAndFlush(category);

            return ResponseEntity.ok(new ResponseMessage("Grupa została utworzona.", ResponseCode.SUCCESS));
        }
        catch(CategoryDataEmptyException | CategoryExistsException | GroupNotFoundException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError()
                    .body(new ResponseMessage("Napotkano na nieoczekiwany błąd podczas tworzenia kategorii.",
                            ResponseCode.ERROR));
        }
    }

    public ResponseEntity<ResponseMessage> editCategoryData(UUID categoryId, NewTicketCategoryDTO categoryData)
    {
        try
        {
            if(categoryId == null) throw new CategoryNotFoundException("Nie podano jaka kategoria ma zostać zmodyfikowana!");
            if(categoryData == null) throw new CategoryDataEmptyException("Dane kategorii nie mogą być puste!");

            Optional<TicketCategory> categoryInDatabase = this.TICKET_CATEGORY_REPOSITORY.findById(categoryId);
            if(categoryInDatabase.isEmpty()) throw new CategoryNotFoundException("Nie odnaleziono wskazanej kategorii!");

            Optional<SupportGroup> groupInDatabase = this.GROUP_SERVICE.getGroupById(categoryData.getDefaultGroup());
            if(groupInDatabase.isEmpty()) throw new GroupNotFoundException("Nie odnaleziono wskazanej grupy wsparcia!");

            TicketCategory category = categoryInDatabase.get();
            SupportGroup group = groupInDatabase.get();

            category.setName(categoryData.getName());
            category.setDescription(categoryData.getDescription());
            category.setTicketType(categoryData.getTicketType());
            category.setDefaultGroup(group);

            this.TICKET_CATEGORY_REPOSITORY.saveAndFlush(category);

            return ResponseEntity.ok(new ResponseMessage("Dane kategorii zostały edytowane.", ResponseCode.SUCCESS));
        }
        catch(CategoryDataEmptyException | CategoryNotFoundException | GroupNotFoundException exception)
        {
            return ResponseEntity.badRequest().body(new ResponseMessage(exception.getMessage(), ResponseCode.ERROR));
        }
        catch(Exception exception)
        {
            return ResponseEntity.internalServerError()
                    .body(new ResponseMessage("Napotkano na nieoczekiwany błąd podczas edycji danych kategorii.",
                            ResponseCode.ERROR));
        }
    }
}
