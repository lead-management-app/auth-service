package com.blitzdev.auth_service.dtos;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
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