package app.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationPreferenceResponse {

    private UUID id;

    private UUID userId;

    private boolean enabled;

    private String contactInfo;
}
