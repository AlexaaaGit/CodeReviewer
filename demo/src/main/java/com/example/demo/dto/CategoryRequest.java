package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body for creating or updating a category.
 */
public record CategoryRequest(
        @NotBlank(message = "Category name is required")
        String name
) {}
