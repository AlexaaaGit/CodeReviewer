package com.example.demo.service;

import com.example.demo.dto.CommentResponse;
import com.example.demo.dto.ProductResponse;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.UserUpdateRequest;

import java.util.List;

/**
 * Service interface for user management (admin features) and user profile queries.
 */
public interface UserService {

    /** Returns all registered users. */
    List<UserResponse> getAllUsers();

    /** Updates the role of a user. Only admins can call this. */
    UserResponse updateUserRole(Long userId, UserUpdateRequest request);

    /** Returns all non-deleted projects submitted by a given user. */
    List<ProductResponse> getProjectsByUser(Long userId);

    /** Returns all non-deleted code reviews left by a given user. */
    List<CommentResponse> getCommentsByUser(Long userId);
}
