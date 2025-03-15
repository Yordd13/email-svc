package app.service;

import app.model.NotificationPreference;
import app.repository.NotificationPreferenceRepository;
import app.web.dto.UpsertNotificationPreference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationPreferenceRepository preferenceRepository;

    @Autowired
    public NotificationService(NotificationPreferenceRepository notificationPreferenceRepository) {
        this.preferenceRepository = notificationPreferenceRepository;
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
}
