package app.service;

import app.model.Notification;
import app.model.NotificationPreference;
import app.model.NotificationStatus;
import app.repository.NotificationPreferenceRepository;
import app.repository.NotificationRepository;
import app.web.dto.NotificationRequest;
import app.web.dto.UpsertNotificationPreference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationPreferenceRepository preferenceRepository;
    private final NotificationRepository notificationRepository;
    private final MailSender mailSender;

    @Autowired
    public NotificationService(NotificationPreferenceRepository notificationPreferenceRepository, NotificationRepository notificationRepository, MailSender mailSender) {
        this.preferenceRepository = notificationPreferenceRepository;
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
    }

    public NotificationPreference upsertPreference(UpsertNotificationPreference dto) {

        Optional<NotificationPreference> userPreferenceOptional = preferenceRepository.findByUserId(dto.getUserId());

        if(userPreferenceOptional.isPresent()) {
            NotificationPreference userPreference = userPreferenceOptional.get();
            userPreference.setEnabled(dto.isNotificationEnabled());
            userPreference.setUpdatedOn(LocalDateTime.now());
            return preferenceRepository.save(userPreference);
        }

        NotificationPreference notificationPreference =
                NotificationPreference
                        .builder()
                        .userId(dto.getUserId())
                        .contactInfo(dto.getContactInfo())
                        .createdOn(LocalDateTime.now())
                        .updatedOn(LocalDateTime.now())
                        .isEnabled(dto.isNotificationEnabled())
                        .build();

        return preferenceRepository.save(notificationPreference);
    }

    public NotificationPreference getPreferenceByUserId(UUID userId) {
        return preferenceRepository.findByUserId(userId).orElseThrow(() ->
                new NullPointerException("User with id " + userId + " not found."));
    }

    public Notification sendNotification(NotificationRequest request) {

        UUID userId = request.getUserId();
        NotificationPreference preference = getPreferenceByUserId(userId);

        if(!preference.isEnabled()) {
            throw new IllegalArgumentException("User with id " + userId + " does not allow to receive notifications.");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(preference.getContactInfo());
        message.setSubject(request.getSubject());
        message.setText(request.getBody());

        Notification notification = Notification
                .builder()
                .subject(request.getSubject())
                .body(request.getBody())
                .createdOn(LocalDateTime.now())
                .userId(userId)
                .build();

        try{
            mailSender.send(message);
            notification.setStatus(NotificationStatus.SUCCEEDED);
        }catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
        }

        return notificationRepository.save(notification);
    }
}
