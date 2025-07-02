package com.store.elara.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.store.elara.entities.User;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"profile", "addresses", "favoriteProducts"})
    List<User> findAll(Sort sort);

    @EntityGraph(attributePaths = "profile")
    Optional<User> findById(Long id);

    Optional<User> findByName(String name);

    User findUserByName(String username);
}
