package com.store.elara.mappers;

import com.store.elara.dtos.CartItemsDto;
import com.store.elara.entities.CartItems;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartItemsMapper {
    CartItemsDto toCartItemsDto(CartItems cartItems);

    CartItems toCartItems(CartItemsDto cartItemsDto);
}
