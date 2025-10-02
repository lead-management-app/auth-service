package com.blitzdev.auth_service.dtos;

import lombok.*;

@Getter
@Setter
public class LoginUserDto {
    private String email;
    private String password;
}
