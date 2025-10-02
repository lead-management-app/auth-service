package com.blitzdev.auth_service.dtos;

import lombok.*;

@Getter
@Setter
public class RegisterUserDto {
    private String name;
    private String email;
    private String password;
}