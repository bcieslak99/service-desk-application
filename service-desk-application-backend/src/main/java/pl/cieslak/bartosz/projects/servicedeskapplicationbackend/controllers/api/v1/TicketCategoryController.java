package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.controllers.api.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.categories.CategoryDetailsDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.categories.NewTicketCategoryDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.responses.ResponseCode;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.responses.ResponseMessage;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketType;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.tickets.TicketCategoryService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/category")
public class TicketCategoryController
{
    private final TicketCategoryService TICKET_CATEGORY_SERVICE;

    @GetMapping("/list/all")
    @PreAuthorize("hasAuthority('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<List<CategoryDetailsDTO>> getAllCategories()
    {
        return ResponseEntity.ok(this.TICKET_CATEGORY_SERVICE.getAllCategories());
    }

    @GetMapping("/list/all/serviceRequests")
    @PreAuthorize("hasAuthority('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<List<CategoryDetailsDTO>> getAllCategoriesForServiceRequests()
    {
        return ResponseEntity.ok(this.TICKET_CATEGORY_SERVICE.getAllCategoriesByTicketType(TicketType.SERVICE_REQUEST));
    }

    @GetMapping("/list/all/incidents")
    @PreAuthorize("hasAuthority('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<List<CategoryDetailsDTO>> getAllCategoriesForIncidents()
    {
        return ResponseEntity.ok(this.TICKET_CATEGORY_SERVICE.getAllCategoriesByTicketType(TicketType.INCIDENT));
    }

    @GetMapping("/list/all/problems")
    @PreAuthorize("hasAuthority('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<List<CategoryDetailsDTO>> getAllCategoriesForProblems()
    {
        return ResponseEntity.ok(this.TICKET_CATEGORY_SERVICE.getAllCategoriesByTicketType(TicketType.PROBLEM));
    }

    @GetMapping("/list/active/serviceRequests")
    @PreAuthorize("hasAuthority('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<List<CategoryDetailsDTO>> getActiveCategoriesForServiceRequests()
    {
        return ResponseEntity.ok(this.TICKET_CATEGORY_SERVICE.getActiveCategoriesByTicketType(TicketType.SERVICE_REQUEST));
    }

    @GetMapping("/list/active/incidents")
    @PreAuthorize("hasAuthority('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<List<CategoryDetailsDTO>> getActiveCategoriesForIncidents()
    {
        return ResponseEntity.ok(this.TICKET_CATEGORY_SERVICE.getActiveCategoriesByTicketType(TicketType.INCIDENT));
    }

    @GetMapping("/list/active/problems")
    @PreAuthorize("hasAuthority('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<List<CategoryDetailsDTO>> getActiveCategoriesForProblems()
    {
        return ResponseEntity.ok(this.TICKET_CATEGORY_SERVICE.getActiveCategoriesByTicketType(TicketType.PROBLEM));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<ResponseMessage> createNewCategory(@Valid @RequestBody NewTicketCategoryDTO categoryData, BindingResult errors)
    {
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(new ResponseMessage("Dane nowej kategorii są nieprawidłowe!",
                    ResponseCode.ERROR));

        return this.TICKET_CATEGORY_SERVICE.createNewCategory(categoryData);
    }

    @PatchMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<ResponseMessage> editCategory(@PathVariable("id") UUID categoryId,
                                                        @Valid @RequestBody NewTicketCategoryDTO categoryData,
                                                        BindingResult errors)
    {
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(new ResponseMessage("Podano nieprawidłowe dane!", ResponseCode.ERROR));

        return this.TICKET_CATEGORY_SERVICE.editCategoryData(categoryId, categoryData);
    }

    @PatchMapping("/activate/{id}")
    @PreAuthorize("hasAuthority('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<ResponseMessage> activateCategory(@PathVariable("id") UUID categoryId)
    {
        return this.TICKET_CATEGORY_SERVICE.activateCategory(categoryId);
    }

    @PatchMapping("/deactivate/{id}")
    @PreAuthorize("hasAuthority('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<ResponseMessage> deactivateCategory(@PathVariable("id") UUID categoryId)
    {
        return this.TICKET_CATEGORY_SERVICE.deactivateCategory(categoryId);
    }
}
