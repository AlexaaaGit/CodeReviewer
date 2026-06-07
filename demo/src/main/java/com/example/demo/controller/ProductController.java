package com.example.demo.controller;

import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository;

    /**
     * GET /api/products?page=0&size=5&title=phone&categoryId=2
     * Returns a paginated list of products, optionally filtered by title and/or category.
     * Server-side pagination is used — the client only receives one page at a time.
     */
    @GetMapping
    public Page<ProductResponse> getAllProducts(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "6")  int size,
            @RequestParam(required = false)    String title,
            @RequestParam(required = false)    Long categoryId) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        return productService.getProducts(title, categoryId, pageable);
    }

    /**
     * GET /api/products/{id}
     * Returns a single product with its category and comment count.
     */
    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    /**
     * POST /api/products
     * Creates a new product. The creator's user ID is extracted from the JWT token.
     * Requires authentication.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('JUNIOR', 'ADMIN')")
    public ProductResponse addProduct(
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Resolve the user ID from the authenticated principal
        Long userId = resolveUserId(userDetails);
        return productService.addProduct(request, userId);
    }

    /**
     * PUT /api/products/{id}
     * Updates an existing product's title, description, imageUrl, and categories.
     * Requires authentication.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('JUNIOR', 'ADMIN')")
    public ProductResponse updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        boolean isAdmin = userDetails != null && userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return productService.updateProduct(id, request, userId, isAdmin);
    }

    /**
     * DELETE /api/products/{id}
     * Soft-deletes a product (sets isDeleted = true).
     * Only users with ADMIN role can perform this action (grade 5 requirement).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
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
