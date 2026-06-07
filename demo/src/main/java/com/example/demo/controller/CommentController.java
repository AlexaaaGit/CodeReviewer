package com.example.demo.controller;

import com.example.demo.dto.CommentRequest;
import com.example.demo.dto.CommentResponse;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing comments on products.
 * Comments are nested under /api/products/{productId}/comments.
 */
@RestController
@RequestMapping("/api/products/{productId}/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    /**
     * GET /api/products/{productId}/comments
     * Returns all non-deleted comments for the given product.
     * Public access — no authentication required.
     */
    @GetMapping
    public List<CommentResponse> getComments(@PathVariable Long productId) {
        return commentService.getCommentsByProductId(productId);
    }

    /**
     * POST /api/products/{productId}/comments
     * Adds a new comment to the product. Creator ID is taken from JWT.
     * Requires authentication.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN')")
    public CommentResponse addComment(
            @PathVariable Long productId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = resolveUserId(userDetails);
        return commentService.addComment(productId, request, userId);
    }

    /**
     * PUT /api/products/{productId}/comments/{commentId}
     * Updates the description of an existing comment.
     * Requires authentication.
     */
    @PutMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN')")
    public CommentResponse updateComment(
            @PathVariable Long productId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        boolean isAdmin = userDetails != null && userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return commentService.updateComment(commentId, request, userId, isAdmin);
    }

    /**
     * DELETE /api/products/{productId}/comments/{commentId}
     * Soft-deletes a comment. Only ADMIN can delete comments.
     */
    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteComment(
            @PathVariable Long productId,
            @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }

    /**
     * Helper to resolve the database user ID from the Spring Security principal.
     */
    private Long resolveUserId(UserDetails userDetails) {
        if (userDetails == null) return null;
        return userRepository.findByUsername(userDetails.getUsername())
                .map(User::getId)
                .orElse(null);
    }
}
