package br.com.frotasyncro.infrastructure.persistence.authentication.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RoleEntity implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String authority;

    @Override
    public String getAuthority() {
        return this.authority;
    }
}
