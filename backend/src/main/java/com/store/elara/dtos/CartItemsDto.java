package com.store.elara.dtos;

import com.store.elara.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartItemsDto {
    private ProductDto productDto;
    private int quantity;
}
