package com.example.demo.service;

import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for product business logic.
 */
public interface ProductService {

    /** Returns a paginated list of non-deleted products, optionally filtered by title and/or category. */
    Page<ProductResponse> getProducts(String title, Long categoryId, Pageable pageable);

    /** Returns a single product by ID. */
    ProductResponse getProductById(Long id);

    /** Creates a new product and assigns categories. */
    ProductResponse addProduct(ProductRequest request, Long creatorUserId);

    /** Updates title, description, imageUrl, and categories of an existing product. */
    ProductResponse updateProduct(Long id, ProductRequest request, Long currentUserId, boolean isAdmin);

    /** Soft-deletes a product by setting isDeleted = true. */
    void deleteProduct(Long id);
}
