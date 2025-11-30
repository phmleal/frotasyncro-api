package br.com.frotasyncro.infrastructure.persistence.authentication.entities;

import br.com.frotasyncro.domain.authentication.enums.UserStatus;
import br.com.frotasyncro.infrastructure.persistence.generic.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

import static br.com.frotasyncro.domain.authentication.enums.UserStatus.CONFIGURATION;
import static br.com.frotasyncro.domain.authentication.enums.UserStatus.INACTIVE;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class UserEntity extends BaseEntity implements UserDetails {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id", table = "users"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id", table = "roles"))
    private Set<RoleEntity> roles;

    @Transient
    private String temporaryPassword;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return getStatus() != CONFIGURATION;
    }

    @Override
    public boolean isEnabled() {
        return getStatus() != INACTIVE;
    }

    public boolean isAdmin() {
        return getRoles().stream().anyMatch(role -> role.getAuthority().toLowerCase().contains("admin"));
    }

}
