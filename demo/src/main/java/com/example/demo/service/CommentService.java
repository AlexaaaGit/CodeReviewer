package com.example.demo.service;

import com.example.demo.dto.CommentRequest;
import com.example.demo.dto.CommentResponse;

import java.util.List;

/**
 * Service interface for comment business logic.
 */
public interface CommentService {

    /** Returns all non-deleted comments for a given product. */
    List<CommentResponse> getCommentsByProductId(Long productId);

    /** Returns all non-deleted comments created by a given user. Used in profile page. */
    List<CommentResponse> getCommentsByUserId(Long userId);

    /** Adds a new comment to a product. */
    CommentResponse addComment(Long productId, CommentRequest request, Long creatorUserId);

    /** Updates the description of an existing comment. */
    CommentResponse updateComment(Long commentId, CommentRequest request, Long currentUserId, boolean isAdmin);

    /** Soft-deletes a comment by setting isDeleted = true. */
    void deleteComment(Long commentId);
}
