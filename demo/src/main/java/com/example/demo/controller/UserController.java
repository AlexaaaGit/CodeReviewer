package com.example.demo.controller;

import com.example.demo.dto.CommentResponse;
import com.example.demo.dto.ProductResponse;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for user management and profile queries.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    /**
     * GET /api/users
     * Returns all registered users. Admin only.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * PUT /api/users/{id}/role
     * Updates the role of a user. Admin only.
     */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        return userService.updateUserRole(id, request);
    }

    /**
     * GET /api/users/me/projects
     * Returns all projects submitted by the currently authenticated user.
     * Accessible to any authenticated user (JUNIOR, MENTOR, ADMIN).
     */
    @GetMapping("/me/projects")
    @PreAuthorize("isAuthenticated()")
    public List<ProductResponse> getMyProjects(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        return userService.getProjectsByUser(userId);
    }

    /**
     * GET /api/users/me/comments
     * Returns all code reviews posted by the currently authenticated user.
     * Accessible to any authenticated user (JUNIOR, MENTOR, ADMIN).
     */
    @GetMapping("/me/comments")
    @PreAuthorize("isAuthenticated()")
    public List<CommentResponse> getMyComments(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        return userService.getCommentsByUser(userId);
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
