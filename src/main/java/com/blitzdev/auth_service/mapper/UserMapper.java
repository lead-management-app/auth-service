package com.blitzdev.auth_service.mapper;

import com.blitzdev.auth_service.domain.User;
import com.blitzdev.auth_service.model.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = DateMapper.class)
public interface UserMapper {

    @Mapping(target = "role", expression = "java(user.getUserRole().getName())")
    UserDto userToUserDto(User user);

    User userDtoToUser(UserDto dto);
}