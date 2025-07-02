package com.store.elara.controllers;

import com.store.elara.entities.Category;
import com.store.elara.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @PostMapping("/add")
    public String addCategory(@RequestBody Category category) {
            categoryRepository.save(category);
            return "Category added successfully";
    }
}
