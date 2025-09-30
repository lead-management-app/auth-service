package com.blitzdev.auth_service.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.Timestamp;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(length = 36, columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Version
    private Long version;

    @Column(updatable = false)
    private Timestamp signUpDate;

    @Column(name = "last_mod_by", nullable = false)
    private UUID lastModBy;

    @Column(name = "last_mod_date", nullable = false)
    private UUID lastModDate;
}