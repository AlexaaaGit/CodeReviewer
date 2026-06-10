package com.example.demo.service;

import com.example.demo.dto.CategoryResponse;
import com.example.demo.dto.CommentResponse;
import com.example.demo.dto.ProductResponse;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.model.Comment;
import com.example.demo.model.Product;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toUserResponse)
                .toList();
    }

    @Override
    public UserResponse updateUserRole(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        Role newRole = parseRole(request.role());
        if (user.getRole() == Role.ROLE_ADMIN
                && newRole != Role.ROLE_ADMIN
                && userRepository.countByRole(Role.ROLE_ADMIN) <= 1) {
            throw new IllegalStateException("At least one administrator account must remain active.");
        }

        user.setRole(newRole);

        return toUserResponse(userRepository.save(user));
    }

    private Role parseRole(String role) {
        try {
            return Role.valueOf(role);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported role: " + role);
        }
    }

    @Override
    public List<ProductResponse> getProjectsByUser(Long userId) {
        return productRepository.findByCreatorUserIdAndIsDeletedFalse(userId)
                .stream()
                .map(this::toProductResponse)
                .toList();
    }

    @Override
    public List<CommentResponse> getCommentsByUser(Long userId) {
        return commentRepository.findByCreatorUserIdAndIsDeletedFalse(userId)
                .stream()
                .map(this::toCommentResponse)
                .toList();
    }

    // ==================== Mappers ====================

    private UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRole().name());
    }

    private ProductResponse toProductResponse(Product product) {
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
                product.isDeleted(),
                product.getCreationDate(),
                product.getCreatorUserId(),
                creatorUsername,
                categoryResponses,
                commentCount
        );
    }

    private CommentResponse toCommentResponse(Comment comment) {
        // Look up the author info for author attribution
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
