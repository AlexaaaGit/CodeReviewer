package com.example.demo.service;

import com.example.demo.dto.CategoryRequest;
import com.example.demo.dto.CategoryResponse;

import java.util.List;

/**
 * Service interface for category business logic.
 */
public interface CategoryService {

    /** Returns all non-deleted categories. */
    List<CategoryResponse> getAllCategories();

    /** Creates a new category. */
    CategoryResponse addCategory(CategoryRequest request);

    /** Updates the name of an existing category. */
    CategoryResponse updateCategory(Long id, CategoryRequest request);

    /** Soft-deletes a category by setting isDeleted = true. */
    void deleteCategory(Long id);
}
