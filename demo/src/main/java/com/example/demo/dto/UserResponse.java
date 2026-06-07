package com.example.demo.dto;

/**
 * Response DTO for user information (used in admin panel).
 */
public record UserResponse(
        Long id,
        String username,
        String role
) {}
