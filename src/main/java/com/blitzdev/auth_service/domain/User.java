package com.blitzdev.auth_service.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lms_user")
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    @Column(name = "name", nullable = false)
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

    @Column(name = "password_delta")
    private int passwordDeltaInd;
}
