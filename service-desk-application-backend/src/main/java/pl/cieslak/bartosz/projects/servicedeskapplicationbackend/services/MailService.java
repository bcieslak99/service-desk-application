package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailService
{
    private final JavaMailSender MAIL_SENDER;
    private final TemplateEngine TEMPLATE_ENGINE;

    private String renderTemplate(String templateName, Map<String, Object> variables)
    {
        Context context = new Context();
        context.setVariables(variables);
        return TEMPLATE_ENGINE.process(templateName, context);
    }

    public void sendReminderAboutTicket(String to, UUID ticketId, String ticketCategoryName, String messageFromCustomer) throws MessagingException
    {
        Map<String, Object> variables = new HashMap<>();
        variables.put("ticketCategory", "Kategoria zgłoszenia: " + ticketCategoryName);
        variables.put("ticketId", "Identyfikator: zgłoszenia: " + ticketId);
        variables.put("message", "Komentarz od użytkownika: " +  messageFromCustomer);

        String content = renderTemplate("ReminderFromCustomer", variables);

        MimeMessage message = MAIL_SENDER.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject("Service Desk :: Monit od użytkownika");
        helper.setText(content, true);
        helper.setValidateAddresses(true);

        MAIL_SENDER.send(message);
    }

    public void sendReminderAboutTask(String to, String supportGroupName, String taskSetTitle, String plannedEndDate) throws MessagingException
    {
        Map<String, Object> variables = new HashMap<>();
        variables.put("taskSetTitle", taskSetTitle);
        variables.put("plannedEndDate", plannedEndDate);
        variables.put("supportGroupName", supportGroupName);

        String content = renderTemplate("TaskReminder", variables);

        MimeMessage message = MAIL_SENDER.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject("Service Desk :: Przypomnienie o zadaniu");
        helper.setText(content, true);
        helper.setValidateAddresses(true);

        MAIL_SENDER.send(message);
    }
}
