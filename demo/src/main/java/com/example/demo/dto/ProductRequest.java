package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * Request body for creating or updating a product.
 */
public record ProductRequest(
        @NotBlank(message = "Title is required")
        String title,

        String description,

        String imageUrl,

        String codeSnippet,

        // List of category IDs to assign to this product
        List<Long> categoryIds
) {}
