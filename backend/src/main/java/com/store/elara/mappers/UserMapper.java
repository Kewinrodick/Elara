package com.store.elara.mappers;

import com.store.elara.dtos.ChangePasswordRequest;
import com.store.elara.dtos.RegisterUserRequest;
import com.store.elara.dtos.UpdateUserRequest;
import com.store.elara.dtos.UserDto;
import com.store.elara.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    User toEntity(RegisterUserRequest request);

    void update(UpdateUserRequest updateUserRequest, @MappingTarget User user);
}
