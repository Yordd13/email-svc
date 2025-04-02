package app.web;

import app.model.Notification;
import app.model.NotificationPreference;
import app.service.EmailService;
import app.web.dto.NotificationPreferenceResponse;
import app.web.dto.NotificationRequest;
import app.web.dto.NotificationResponse;
import app.web.dto.UpsertNotificationPreference;
import app.web.mapper.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class EmailController {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService notificationService) {
        this.emailService = notificationService;
    }

    @PostMapping("/preferences")
    public ResponseEntity<NotificationPreferenceResponse> upsertPreference(@RequestBody UpsertNotificationPreference preference) {

        NotificationPreference notificationPreference =  emailService.upsertPreference(preference);

        NotificationPreferenceResponse response = DtoMapper.fromNotificationPreference(notificationPreference);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/preferences")
    public ResponseEntity<NotificationPreferenceResponse> getUserPreference(@RequestParam(name = "userId") UUID userId) {

        NotificationPreference notificationPreference = emailService.getPreferenceByUserId(userId);

        NotificationPreferenceResponse response = DtoMapper.fromNotificationPreference(notificationPreference);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> sendNotification(@RequestBody NotificationRequest request) {
        Notification notification = emailService.sendNotification(request);

        NotificationResponse response = DtoMapper.fromNotification(notification);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
