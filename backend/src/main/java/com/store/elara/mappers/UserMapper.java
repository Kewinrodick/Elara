package com.store.elara.mappers;

import com.store.elara.dtos.*;
import com.store.elara.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    User toEntity(LoginRequest request);

    void update(UpdateUserRequest updateUserRequest, @MappingTarget User user);
}
