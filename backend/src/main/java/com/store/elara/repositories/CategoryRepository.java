package com.store.elara.repositories;

import com.store.elara.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Byte> {
        Optional<Category> findByName(String name);
}
