package com.store.elara.repositories;

import com.store.elara.entities.Cart;
import com.store.elara.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

}
