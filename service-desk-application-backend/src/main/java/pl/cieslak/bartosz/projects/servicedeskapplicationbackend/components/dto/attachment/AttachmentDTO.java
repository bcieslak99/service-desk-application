package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.attachment;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class AttachmentDTO
{
    private UUID id;
    private String name;
}
