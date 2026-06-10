package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for a product, including its categories and comment count.
 */
public record ProductResponse(
        Long id,
        String title,
        String description,
        String imageUrl,
        String codeSnippet,
        boolean isDeleted,
        LocalDateTime creationDate,
        Long creatorUserId,
        String creatorUsername,
        List<CategoryResponse> categories,
        int commentCount
) {}
