package com.store.elara.dtos;

import com.store.elara.entities.CartItems;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartDto {
    private Long id;
    private List<CartItemsDto> cartItemsDto;
    private BigDecimal totalPrice;

    public BigDecimal calculateTotalPrice() {
        totalPrice = BigDecimal.ZERO;

        for (CartItemsDto cartItems : cartItemsDto) {
            BigDecimal quantity = BigDecimal.valueOf(cartItems.getQuantity());
            totalPrice = totalPrice.add(quantity.multiply(cartItems.getProductDto().getPrice()));
        }
        return totalPrice;
    }
    public BigDecimal getTotalPrice() {
        return calculateTotalPrice(); // always up-to-date
    }
}
