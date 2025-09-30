package com.blitzdev.auth_service.model;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserDto extends BaseDto {

    private UUID id;

    private String name;

    private String email;
}