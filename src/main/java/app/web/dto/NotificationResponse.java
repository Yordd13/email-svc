package app.web.dto;

import app.model.NotificationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {

    private String subject;

    private String body;

    private LocalDateTime createdOn;

    private NotificationStatus status;
}
