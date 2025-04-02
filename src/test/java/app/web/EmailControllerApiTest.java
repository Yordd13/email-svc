package app.web;

import app.model.NotificationPreference;
import app.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailController.class)
public class EmailControllerApiTest {

    @MockitoBean
    private EmailService emailService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRequestNotificationPreference_happyPath() throws Exception {

        NotificationPreference notificationPreference =NotificationPreference.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .isEnabled(true)
                .contactInfo("test@gmail.com")
                .updatedOn(LocalDateTime.now())
                .createdOn(LocalDateTime.now())
                .build();

        when(emailService.getPreferenceByUserId(any())).thenReturn(notificationPreference);

        MockHttpServletRequestBuilder request = get("/api/notifications/preferences")
                .param("userId", UUID.randomUUID().toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("userId").isNotEmpty())
                .andExpect(jsonPath("enabled").isNotEmpty())
                .andExpect(jsonPath("contactInfo").isNotEmpty());
    }
}
