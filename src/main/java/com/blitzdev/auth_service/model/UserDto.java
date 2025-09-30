package com.blitzdev.auth_service.model;

import com.blitzdev.auth_service.domain.UserRole;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserDto extends BaseDto {

    private String name;

    private String email;

    private String role;

    private int lockedInd;

    private int enabledInd;

    private int deltaPasswordInd;

    private boolean isChangePassword() {
        return this.deltaPasswordInd == 1;
    }
}