package com.example.demo.controller;

import com.example.demo.dto.CategoryRequest;
import com.example.demo.dto.CategoryResponse;
import com.example.demo.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing product categories.
 * Only admins can create, update, or delete categories.
 * Listing is public (used for combobox filter on the product page).
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * GET /api/categories
     * Returns all non-deleted categories (no pagination, as per requirements).
     * Public access for use in filter combobox.
     */
    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    /**
     * POST /api/categories
     * Creates a new category. Admin only.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse addCategory(@Valid @RequestBody CategoryRequest request) {
        return categoryService.addCategory(request);
    }

    /**
     * PUT /api/categories/{id}
     * Updates an existing category's name. Admin only.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        return categoryService.updateCategory(id, request);
    }

    /**
     * DELETE /api/categories/{id}
     * Soft-deletes a category. Admin only.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
