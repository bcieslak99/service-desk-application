package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Component
public class ResponseMessage
{
    private String message;
    private ResponseCode code;
    private LocalDateTime timestamp = LocalDateTime.now();

    public ResponseMessage(String message, ResponseCode responseCode)
    {
        this.message = message != null ? message.trim() : null;
        this.code = responseCode;
    }
}
