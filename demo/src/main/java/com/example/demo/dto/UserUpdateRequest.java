package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body for updating a user's role (admin only).
 */
public record UserUpdateRequest(
        @NotBlank(message = "Role is required")
        String role
) {}
