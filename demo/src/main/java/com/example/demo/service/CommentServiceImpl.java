package com.example.demo.service;

import com.example.demo.dto.CommentRequest;
import com.example.demo.dto.CommentResponse;
import com.example.demo.model.Comment;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<CommentResponse> getCommentsByProductId(Long productId) {
        return commentRepository.findByProductIdAndIsDeletedFalse(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<CommentResponse> getCommentsByUserId(Long userId) {
        return commentRepository.findByCreatorUserIdAndIsDeletedFalse(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CommentResponse addComment(Long productId, CommentRequest request, Long creatorUserId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + productId));

        Comment comment = new Comment();
        comment.setDescription(request.description());
        comment.setProduct(product);
        comment.setCreatorUserId(creatorUserId);

        return toResponse(commentRepository.save(comment));
    }

    @Override
    public CommentResponse updateComment(Long commentId, CommentRequest request, Long currentUserId, boolean isAdmin) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found with id: " + commentId));

        // Ownership validation: MENTOR can only edit their own reviews
        if (!isAdmin && (comment.getCreatorUserId() == null || !comment.getCreatorUserId().equals(currentUserId))) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to edit this comment.");
        }

        comment.setDescription(request.description());
        return toResponse(commentRepository.save(comment));
    }

    @Override
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found with id: " + commentId));
        comment.setDeleted(true);
        commentRepository.save(comment);
    }

    /**
     * Converts a Comment entity to a CommentResponse DTO.
     * Looks up the author's username and role for mentor attribution display.
     */
    private CommentResponse toResponse(Comment comment) {
        String authorUsername = null;
        String authorRole = null;

        if (comment.getCreatorUserId() != null) {
            User author = userRepository.findById(comment.getCreatorUserId()).orElse(null);
            if (author != null) {
                authorUsername = author.getUsername();
                authorRole = author.getRole().name();
            }
        }

        return new CommentResponse(
                comment.getId(),
                comment.getDescription(),
                comment.getCreationDate(),
                comment.getCreatorUserId(),
                authorUsername,
                authorRole,
                comment.getProduct().getId()
        );
    }
}
