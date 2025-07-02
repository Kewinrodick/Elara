package com.store.elara.repositories;

import com.store.elara.entities.CartItems;
import com.store.elara.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemsRepository extends JpaRepository<CartItems, Long> {

    public List<CartItems> findAllByProduct(Product product);
}
