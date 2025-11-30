package br.com.frotasyncro.infrastructure.persistence.authentication.entities;

import br.com.frotasyncro.domain.authentication.enums.OtpStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "otps")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OtpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false, updatable = false)
    private String hash;

    @Column(nullable = false, updatable = false)
    private String salt;

    @Column(nullable = false)
    private Integer attempts;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status")
    private OtpStatus otpStatus;

    @Column(name = "expires_at", nullable = false, updatable = false)
    private LocalDateTime expiresAt;

    @Column(name = "last_attempt_at")
    private LocalDateTime lastAttemptAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

}
