package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a product that can be reviewed.
 * Has one-to-many relationship with Comment and many-to-many with Category.
 */
@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String codeSnippet;

    // Soft delete flag — products are never physically removed from DB
    private boolean isDeleted = false;

    private LocalDateTime creationDate = LocalDateTime.now();

    // ID of the user who created this product (grade 5 requirement)
    private Long creatorUserId;

    // One product has many comments (reviews)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("product")
    private List<Comment> comments = new ArrayList<>();

    // Many products can belong to many categories (owning side)
    @ManyToMany
    @JoinTable(
            name = "product_categories",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @JsonIgnoreProperties("products")
    private List<Category> categories = new ArrayList<>();
}
