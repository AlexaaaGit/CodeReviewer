package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body for creating or updating a comment.
 */
public record CommentRequest(
        @NotBlank(message = "Comment description is required")
        String description
) {}
