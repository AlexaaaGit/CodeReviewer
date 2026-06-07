package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a product category.
 * Many-to-many relationship with Product via the join table "product_categories".
 */
@Entity
@Table(name = "categories")
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Soft delete flag — categories are never physically removed from DB
    private boolean isDeleted = false;

    // Many categories can contain many products (owning side is Product)
    @ManyToMany(mappedBy = "categories")
    @JsonIgnoreProperties({"categories", "comments"})
    private List<Product> products = new ArrayList<>();
}
