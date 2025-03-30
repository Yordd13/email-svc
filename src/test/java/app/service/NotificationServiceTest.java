package app.service;

import app.model.Notification;
import app.model.NotificationPreference;
import app.model.NotificationStatus;
import app.repository.NotificationPreferenceRepository;
import app.repository.NotificationRepository;
import app.web.dto.NotificationRequest;
import app.web.dto.UpsertNotificationPreference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationPreferenceRepository preferenceRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private MailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void givenExistingPreference_whenUpsertPreference_thenUpdateExistingPreference() {

        // Given
        UUID userId = UUID.randomUUID();
        UpsertNotificationPreference dto = UpsertNotificationPreference.builder()
                .userId(userId)
                .notificationEnabled(true)
                .build();

        NotificationPreference existingPreference = NotificationPreference.builder()
                .userId(userId)
                .contactInfo("test@example.com")
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .isEnabled(false)
                .build();

        when(preferenceRepository.findByUserId(userId)).thenReturn(Optional.of(existingPreference));
        when(preferenceRepository.save(any(NotificationPreference.class))).thenReturn(existingPreference);

        // When
        NotificationPreference result = notificationService.upsertPreference(dto);

        // Then
        assertNotNull(result);
        assertEquals(dto.isNotificationEnabled(), result.isEnabled());
        verify(preferenceRepository).save(existingPreference);

    }

    @Test
    void givenNoExistingPreference_whenUpsertPreference_thenCreateNewPreference() {

        // Given
        UUID userId = UUID.randomUUID();
        UpsertNotificationPreference dto = UpsertNotificationPreference.builder()
                .userId(userId)
                .contactInfo("test@example.com")
                .notificationEnabled(true)
                .build();

        when(preferenceRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(preferenceRepository.save(any(NotificationPreference.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

        // When
        NotificationPreference result = notificationService.upsertPreference(dto);

        // Then
        assertNotNull(result);
        assertEquals(dto.isNotificationEnabled(), result.isEnabled());
        assertEquals(dto.getContactInfo(), result.getContactInfo());
        verify(preferenceRepository).save(any(NotificationPreference.class));

    }

    @Test
    void givenExistingUser_whenGetPreferenceByUserId_thenReturnPreference() {

        // Given
        UUID userId = UUID.randomUUID();
        NotificationPreference preference = NotificationPreference.builder()
                .userId(userId)
                .contactInfo("test@example.com")
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .isEnabled(true)
                .build();

        when(preferenceRepository.findByUserId(userId)).thenReturn(Optional.of(preference));

        // When
        NotificationPreference result = notificationService.getPreferenceByUserId(userId);

        // Then
        assertNotNull(result);
        assertEquals(preference.getUserId(), result.getUserId());
        assertEquals(preference.getContactInfo(), result.getContactInfo());

    }

    @Test
    void givenNonExistingUser_whenGetPreferenceByUserId_thenThrowException() {

        // Given
        UUID userId = UUID.randomUUID();
        when(preferenceRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NullPointerException.class, () -> notificationService.getPreferenceByUserId(userId));

    }

    @Test
    void givenEnabledPreference_whenSendNotification_thenSendEmailAndReturnNotification() {

        // Given
        UUID userId = UUID.randomUUID();
        NotificationRequest request = NotificationRequest.builder()
                .userId(userId)
                .subject("Test Subject")
                .body("Test Body")
                .build();

        NotificationPreference preference = NotificationPreference.builder()
                .userId(userId)
                .contactInfo("test@example.com")
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .isEnabled(true)
                .build();

        Notification notification = Notification.builder()
                .userId(userId)
                .subject("Test Subject")
                .body("Test Body")
                .createdOn(LocalDateTime.now())
                .status(NotificationStatus.SUCCEEDED)
                .build();

        when(preferenceRepository.findByUserId(userId)).thenReturn(Optional.of(preference));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        Notification result = notificationService.sendNotification(request);

        // Then
        assertNotNull(result);
        assertEquals(NotificationStatus.SUCCEEDED, result.getStatus());
        verify(mailSender).send(any(SimpleMailMessage.class));

    }

    @Test
    void givenDisabledPreference_whenSendNotification_thenThrowException() {

        // Given
        UUID userId = UUID.randomUUID();
        NotificationRequest request = NotificationRequest.builder()
                .userId(userId)
                .subject("Test Subject")
                .body("Test Body")
                .build();

        NotificationPreference preference = NotificationPreference.builder()
                .userId(userId)
                .contactInfo("test@example.com")
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .isEnabled(false)
                .build();

        when(preferenceRepository.findByUserId(userId)).thenReturn(Optional.of(preference));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> notificationService.sendNotification(request));

    }

    @Test
    void givenErrorWhileSendingEmail_whenSendNotification_thenReturnFailedStatus() {

        // Given
        UUID userId = UUID.randomUUID();
        NotificationRequest request = NotificationRequest.builder()
                .userId(userId)
                .subject("Test Subject")
                .body("Test Body")
                .build();

        NotificationPreference preference = NotificationPreference.builder()
                .userId(userId)
                .contactInfo("test@example.com")
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .isEnabled(true)
                .build();

        Notification notification = Notification.builder()
                .userId(userId)
                .subject("Test Subject")
                .body("Test Body")
                .createdOn(LocalDateTime.now())
                .status(NotificationStatus.FAILED)
                .build();

        when(preferenceRepository.findByUserId(userId)).thenReturn(Optional.of(preference));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // Simulate email sending failure
        doThrow(new RuntimeException("Mail sending failed")).when(mailSender).send(any(SimpleMailMessage.class));

        // When
        Notification result = notificationService.sendNotification(request);

        // Then
        assertNotNull(result);
        assertEquals(NotificationStatus.FAILED, result.getStatus());
        verify(mailSender).send(any(SimpleMailMessage.class));

    }
}
