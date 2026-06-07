package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Server-side filtering by title + pagination (only non-deleted products)
    Page<Product> findByTitleContainingIgnoreCaseAndIsDeletedFalse(String title, Pageable pageable);

    // Server-side pagination, only non-deleted products
    Page<Product> findByIsDeletedFalse(Pageable pageable);

    // Filter by category ID (only non-deleted products)
    Page<Product> findByCategoriesIdAndIsDeletedFalse(Long categoryId, Pageable pageable);

    // Filter by both title and category ID (only non-deleted products)
    Page<Product> findByTitleContainingIgnoreCaseAndCategoriesIdAndIsDeletedFalse(String title, Long categoryId, Pageable pageable);

    // Find all non-deleted products by a specific creator (used in User Profile)
    List<Product> findByCreatorUserIdAndIsDeletedFalse(Long creatorUserId);
}
