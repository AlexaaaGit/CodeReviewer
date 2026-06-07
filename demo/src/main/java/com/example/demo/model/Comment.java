package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represents a review comment attached to a product.
 * Many comments belong to one product.
 */
@Entity
@Table(name = "comments")
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    // Soft delete flag — comments are never physically removed from DB
    private boolean isDeleted = false;

    private LocalDateTime creationDate = LocalDateTime.now();

    // ID of the user who wrote this comment (grade 5 requirement)
    private Long creatorUserId;

    // Many comments belong to one product
    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;
}