package com.blitzdev.auth_service.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String name;
    private String emailAddress;
    private String token;
    private int changePasswordInd;
    private boolean changePassword;

    public boolean isChangePassword() {
        changePassword = this.changePasswordInd == 1;
        return changePassword;
    }
}
