package com.example.demo.service;

import com.example.demo.dto.CategoryResponse;
import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Page<ProductResponse> getProducts(String title, Long categoryId, Pageable pageable) {
        Page<Product> page;

        boolean hasTitle = title != null && !title.isBlank();
        boolean hasCategory = categoryId != null;

        if (hasTitle && hasCategory) {
            // Filter by both title and category
            page = productRepository.findByTitleContainingIgnoreCaseAndCategoriesIdAndIsDeletedFalse(title, categoryId, pageable);
        } else if (hasTitle) {
            // Search by title only (case-insensitive), only non-deleted products
            page = productRepository.findByTitleContainingIgnoreCaseAndIsDeletedFalse(title, pageable);
        } else if (hasCategory) {
            // Filter by category only
            page = productRepository.findByCategoriesIdAndIsDeletedFalse(categoryId, pageable);
        } else {
            // No filters — all non-deleted products, paginated
            page = productRepository.findByIsDeletedFalse(pageable);
        }

        return page.map(this::toResponse);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
        return toResponse(product);
    }

    @Override
    public ProductResponse addProduct(ProductRequest request, Long creatorUserId) {
        Product product = new Product();
        product.setTitle(request.title());
        product.setDescription(request.description());
        product.setImageUrl(request.imageUrl());
        product.setCreatorUserId(creatorUserId);

        // Assign categories if provided
        assignCategories(product, request.categoryIds());

        return toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request, Long currentUserId, boolean isAdmin) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));

        // Ownership validation: JUNIOR can only edit their own projects
        if (!isAdmin && (product.getCreatorUserId() == null || !product.getCreatorUserId().equals(currentUserId))) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to edit this project.");
        }

        product.setTitle(request.title());
        product.setDescription(request.description());
        product.setImageUrl(request.imageUrl());

        // Re-assign categories
        assignCategories(product, request.categoryIds());

        return toResponse(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
        product.setDeleted(true);
        productRepository.save(product);
    }

    /**
     * Assigns categories from a list of IDs to the given product.
     * Clears previous assignments and sets new ones.
     */
    private void assignCategories(Product product, List<Long> categoryIds) {
        if (categoryIds != null && !categoryIds.isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(categoryIds);
            product.setCategories(categories);
        } else {
            product.setCategories(new ArrayList<>());
        }
    }

    /**
     * Converts a Product entity to a ProductResponse DTO.
     * Counts only non-deleted comments.
     */
    private ProductResponse toResponse(Product product) {
        List<CategoryResponse> categoryResponses = product.getCategories() != null
                ? product.getCategories().stream()
                    .filter(c -> !c.isDeleted())
                    .map(c -> new CategoryResponse(c.getId(), c.getName()))
                    .toList()
                : List.of();

        int commentCount = product.getComments() != null
                ? (int) product.getComments().stream().filter(c -> !c.isDeleted()).count()
                : 0;

        return new ProductResponse(
                product.getId(),
                product.getTitle(),
                product.getDescription(),
                product.getImageUrl(),
                product.isDeleted(),
                product.getCreationDate(),
                product.getCreatorUserId(),
                categoryResponses,
                commentCount
        );
    }
}
