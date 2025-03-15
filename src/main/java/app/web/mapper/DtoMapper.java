package app.web.mapper;

import app.model.NotificationPreference;
import app.web.dto.NotificationPreferenceResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {

    public static NotificationPreferenceResponse fromNotificationPreference(NotificationPreference notificationPreference) {
        return NotificationPreferenceResponse
                .builder()
                .id(notificationPreference.getId())
                .enabled(notificationPreference.isEnabled())
                .contactInfo(notificationPreference.getContactInfo())
                .userId(notificationPreference.getUserId())
                .build();
    }
}
