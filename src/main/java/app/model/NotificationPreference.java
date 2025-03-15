package app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique=true, nullable=false)
    private UUID userId;

    @Column(nullable=false)
    private boolean isEnabled;

    @Column(nullable=false, unique=true)
    private String contactInfo;

    @Column(nullable=false)
    private LocalDateTime updatedOn;

    @Column(nullable=false)
    private LocalDateTime createdOn;
}
