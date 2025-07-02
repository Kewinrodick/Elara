package com.store.elara.repositories;

import com.store.elara.entities.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    @EntityGraph(attributePaths = "category")
    public List<Product> findByCategoryId(Byte CategoryId);

    @EntityGraph(attributePaths = "category")
    @Query("select p from Product p")
    public List<Product> fetchAllProductsByCategory();

    @Modifying
    @Query("DELETE FROM Product p WHERE p.category.id = :categoryId")
    void deleteAllByCategory(@Param("categoryId") Byte categoryId);


    @Query("SELECT p FROM Product p WHERE" +
            " LOWER(p.name) LIKE LOWER(CONCAT('%',:keyword,'%')) OR " +
            " LOWER(p.description) LIKE LOWER(CONCAT('%',:keyword,'%')) OR " +
            " LOWER(p.category.name) LIKE LOWER(CONCAT('%',:keyword,'%'))")
    public List<Product> searchByKeyword(String keyword);


}
