package com.blitzdev.auth_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lms_user")
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity implements UserDetails {

    @Column(name = "name", nullable = false, length = 250)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false, unique = true)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column(name = "locked", columnDefinition = "int default 0")
    private int lockedInd;

    @Column(name = "enabled", columnDefinition = "int default 1")
    private int enabledInd;

    @Column(name = "delta_password")
    private int deltaPasswordInd;

    @Column(name = "confirmation_code")
    private String confirmationCode;

    @Column(name = "confirmation_code_expiration")
    private LocalDateTime confirmationCodeExpiration;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userRole.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.lockedInd == 0;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabledInd == 0;
    }
}
