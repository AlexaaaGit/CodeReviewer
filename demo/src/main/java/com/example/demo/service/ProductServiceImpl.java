package com.example.demo.service;

import com.example.demo.dto.CategoryResponse;
import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.model.ReviewStatus;
import com.example.demo.model.SubmissionType;
import com.example.demo.model.User;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(String title, Long categoryId, ReviewStatus status, Pageable pageable) {
        ReviewStatus effectiveStatus = status == null ? ReviewStatus.ALL : status;
        String normalizedTitle = title == null || title.isBlank() ? null : title.trim();

        return productRepository.findFiltered(
                        normalizedTitle,
                        categoryId,
                        effectiveStatus == ReviewStatus.REVIEWED,
                        effectiveStatus == ReviewStatus.NEEDS_REVIEW,
                        pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
        return toResponse(product);
    }

    @Override
    public ProductResponse addProduct(ProductRequest request, Long creatorUserId) {
        Product product = new Product();
        product.setTitle(request.title());
        product.setDescription(request.description());
        product.setImageUrl(request.imageUrl());
        product.setCodeSnippet(request.codeSnippet());
        product.setSubmissionType(request.submissionType());
        product.setSourceUrl(request.sourceUrl());
        product.setCreatorUserId(creatorUserId);

        assignCategories(product, request.categoryIds());

        return toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request, Long currentUserId, boolean isAdmin) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));

        if (!isAdmin && (product.getCreatorUserId() == null || !product.getCreatorUserId().equals(currentUserId))) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to edit this project.");
        }

        product.setTitle(request.title());
        product.setDescription(request.description());
        product.setImageUrl(request.imageUrl());
        product.setCodeSnippet(request.codeSnippet());
        product.setSubmissionType(request.submissionType());
        product.setSourceUrl(request.sourceUrl());

        assignCategories(product, request.categoryIds());

        return toResponse(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
        product.setDeleted(true);
        productRepository.save(product);
    }

    private void assignCategories(Product product, List<Long> categoryIds) {
        if (categoryIds != null && !categoryIds.isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(categoryIds);
            product.setCategories(categories);
        } else {
            product.setCategories(new ArrayList<>());
        }
    }

    private ProductResponse toResponse(Product product) {
        String creatorUsername = null;
        if (product.getCreatorUserId() != null) {
            creatorUsername = userRepository.findById(product.getCreatorUserId())
                    .map(User::getUsername)
                    .orElse(null);
        }

        List<CategoryResponse> categoryResponses = product.getCategories() != null
                ? product.getCategories().stream()
                    .filter(c -> !c.isDeleted())
                    .map(c -> new CategoryResponse(c.getId(), c.getName()))
                    .toList()
                : List.of();

        int commentCount = product.getComments() != null
                ? (int) product.getComments().stream().filter(c -> !c.isDeleted()).count()
                : 0;

        return new ProductResponse(
                product.getId(),
                product.getTitle(),
                product.getDescription(),
                product.getImageUrl(),
                product.getCodeSnippet(),
                product.getSubmissionType() != null ? product.getSubmissionType().name() : SubmissionType.PASTE.name(),
                product.getSourceUrl(),
                product.isDeleted(),
                product.getCreationDate(),
                product.getCreatorUserId(),
                creatorUsername,
                categoryResponses,
                commentCount
        );
    }

}
