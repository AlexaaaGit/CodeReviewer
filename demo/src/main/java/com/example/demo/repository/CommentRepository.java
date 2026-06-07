package com.example.demo.repository;

import com.example.demo.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Finds all non-deleted comments belonging to a specific product.
     * Used to list reviews for a given product.
     */
    List<Comment> findByProductIdAndIsDeletedFalse(Long productId);

    /**
     * Finds all non-deleted comments created by a specific user.
     * Used in the Mentor profile page to list their code reviews.
     */
    List<Comment> findByCreatorUserIdAndIsDeletedFalse(Long creatorUserId);
}
