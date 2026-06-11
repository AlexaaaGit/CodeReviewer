package com.example.demo.dto;

import com.example.demo.model.SubmissionType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
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

        @NotNull(message = "Submission type is required")
        SubmissionType submissionType,

        String sourceUrl,

        // List of category IDs to assign to this product
        List<Long> categoryIds
) {
    @AssertTrue(message = "Code or a valid HTTP/HTTPS source URL is required")
    public boolean isSourceValid() {
        if (submissionType == null) {
            return true;
        }
        if (submissionType == SubmissionType.PASTE) {
            return codeSnippet != null && !codeSnippet.isBlank();
        }
        return isHttpUrl(sourceUrl);
    }

    private boolean isHttpUrl(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            URI uri = URI.create(value.trim());
            String scheme = uri.getScheme();
            return uri.getHost() != null
                    && ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme));
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
