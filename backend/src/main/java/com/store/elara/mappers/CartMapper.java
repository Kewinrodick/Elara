package com.store.elara.mappers;

import com.store.elara.dtos.CartDto;
import com.store.elara.entities.Cart;
import com.store.elara.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartDto toCartDto(Cart cart);


}
