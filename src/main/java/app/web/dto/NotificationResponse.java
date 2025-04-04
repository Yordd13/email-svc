package app.web.dto;

import app.model.EmailStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

    private String subject;

    private String body;

    private LocalDateTime createdOn;

    private EmailStatus status;
}
