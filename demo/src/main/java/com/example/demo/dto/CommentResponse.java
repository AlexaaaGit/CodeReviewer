package com.example.demo.dto;

import java.time.LocalDateTime;

/**
 * Response DTO for a comment, including author identity for mentor attribution.
 */
public record CommentResponse(
        Long id,
        String description,
        LocalDateTime creationDate,
        Long creatorUserId,
        String authorUsername,
        String authorRole,
        Long productId
) {}
