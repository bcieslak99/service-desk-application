package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.tickets;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.attachment.AttachmentDTO;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.Ticket;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tickets.TicketAttachment;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.ticket.TicketNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.users.UserNotFoundException;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.configuration.starters.DirectoryForAttachments;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.tickets.TicketAttachmentRepository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.tickets.TicketRepository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.user.UserService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketAttachmentService
{
    private final TicketAttachmentRepository TICKET_ATTACHMENT_REPOSITORY;
    private final UserService USER_SERVICE;
    private final TicketRepository TICKET_REPOSITORY;

    private final static String TICKET_NOT_FOUND_MESSAGE = "Nie odnaleziono wskazanego zgłoszenia!";
    private final static String USER_NOT_FOUND_MESSAGE = "Nie rozpoznano Twojego konta!";
    private final static String FILE_NOT_FOUND_MESSAGE = "Nie odnaleziono wskazanego pliku!";

    public void uploadAttachment(UUID ticketId, Principal principal, MultipartFile file) throws Exception
    {
        if(ticketId == null) throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);
        if(principal == null || principal.getName() == null || principal.getName().trim().isEmpty())
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        Optional<Ticket> ticketInDatabase = this.TICKET_REPOSITORY.getTicketDetailsById(ticketId);
        if(ticketInDatabase.isEmpty())
            throw new TicketNotFoundException(TICKET_NOT_FOUND_MESSAGE);

        Optional<User> userInDataBase = this.USER_SERVICE.getUserById(this.USER_SERVICE.extractUserId(principal));
        if(userInDataBase.isEmpty()) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);

        Ticket ticket = ticketInDatabase.get();
        User user = userInDataBase.get();

        TicketAttachment ticketAttachment = new TicketAttachment();
        ticketAttachment.setFileOwner(user);
        ticketAttachment.setTicket(ticket);
        ticketAttachment.setName(file.getOriginalFilename());
        this.TICKET_ATTACHMENT_REPOSITORY.saveAndFlush(ticketAttachment);

        File directory = new File(DirectoryForAttachments.DIRECTORY_NAME + File.separator + ticketAttachment.getId());
        if(!directory.exists())
            if(!directory.mkdirs())
                throw new IOException("Nie udało się utworzyć katalogu dla pliku!");

        Path path = Paths.get(DirectoryForAttachments.DIRECTORY_NAME + File.separator + ticketAttachment.getId() + File.separator + file.getOriginalFilename());
        Files.copy(file.getInputStream(), path);
    }

    public ResponseEntity<Resource> downloadAttachment(UUID fileId) throws Exception
    {
        if(fileId == null) throw new FileNotFoundException(FILE_NOT_FOUND_MESSAGE);

        Optional<TicketAttachment> attachmentInDatabase = this.TICKET_ATTACHMENT_REPOSITORY.getTicketAttachmentById(fileId);
        if(attachmentInDatabase.isEmpty()) throw new FileNotFoundException(FILE_NOT_FOUND_MESSAGE);
        TicketAttachment ticketAttachment = attachmentInDatabase.get();

        Path path = Paths.get(DirectoryForAttachments.DIRECTORY_NAME + File.separator + ticketAttachment.getId()).resolve(ticketAttachment.getName()).normalize();
        Resource resource = new UrlResource(path.toUri());

        if(!resource.exists() || !resource.isReadable())
            throw new FileNotFoundException(FILE_NOT_FOUND_MESSAGE);

        String contentType = Files.probeContentType(path);
        if (contentType == null || contentType.isEmpty()) contentType = "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    public List<AttachmentDTO> getAttachmentsListFromTicket(UUID ticketId)
    {
        List<TicketAttachment> attachmentsInDatabase = this.TICKET_ATTACHMENT_REPOSITORY.getAttachmentsListFromTicket(ticketId);
        ArrayList<AttachmentDTO> attachments = new ArrayList<>();
        if(!attachmentsInDatabase.isEmpty()) attachments.ensureCapacity(attachmentsInDatabase.size());
        attachmentsInDatabase.forEach(attachment -> attachments.add(attachment.prepareDetails()));
        return attachments;
    }
}
