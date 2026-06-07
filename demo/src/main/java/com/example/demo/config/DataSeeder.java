package com.example.demo.config;

import com.example.demo.model.*;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * Seeds the database with initial test data on application startup.
 * Only runs when the database is empty (e.g., first launch with in-memory H2).
 */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(
            ProductRepository productRepository,
            CommentRepository commentRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            // Seed admin, junior, and mentor user accounts
            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ROLE_ADMIN);
                userRepository.save(admin);

                User juniorUser = new User();
                juniorUser.setUsername("user");
                juniorUser.setPassword(passwordEncoder.encode("user123"));
                juniorUser.setRole(Role.ROLE_JUNIOR);
                userRepository.save(juniorUser);

                User mentorUser = new User();
                mentorUser.setUsername("mentor");
                mentorUser.setPassword(passwordEncoder.encode("mentor123"));
                mentorUser.setRole(Role.ROLE_MENTOR);
                userRepository.save(mentorUser);

                System.out.println("Seeded default users: admin/admin123, user/user123, mentor/mentor123");
            }

            // Seed categories
            if (categoryRepository.count() == 0) {
                Category frontend = new Category();
                frontend.setName("Frontend");

                Category backend = new Category();
                backend.setName("Backend");

                Category mobile = new Category();
                mobile.setName("Mobile");

                Category gameDev = new Category();
                gameDev.setName("Game Dev");

                Category devOps = new Category();
                devOps.setName("DevOps");

                categoryRepository.saveAll(List.of(frontend, backend, mobile, gameDev, devOps));
                System.out.println("Seeded 5 categories.");
            }

            // Seed products with category assignments
            if (productRepository.count() == 0) {
                System.out.println("Database is empty. Starting automatic seeding...");

                // Load categories for assignment
                List<Category> allCategories = categoryRepository.findAll();
                Category frontendCat = allCategories.stream()
                        .filter(c -> c.getName().equals("Frontend")).findFirst().orElse(null);
                Category backendCat = allCategories.stream()
                        .filter(c -> c.getName().equals("Backend")).findFirst().orElse(null);
                Category gameDevCat = allCategories.stream()
                        .filter(c -> c.getName().equals("Game Dev")).findFirst().orElse(null);

                Product p1 = new Product();
                p1.setTitle("Netflix Clone (React)");
                p1.setDescription("I built a Netflix clone. Please review the login module.");
                p1.setCreatorUserId(2L); // regular user
                if (frontendCat != null) p1.setCategories(List.of(frontendCat));

                Product p2 = new Product();
                p2.setTitle("Spring Boot Bank API");
                p2.setDescription("My first REST API. Am I using the DTO pattern correctly?");
                p2.setCreatorUserId(2L);
                if (backendCat != null) p2.setCategories(List.of(backendCat));

                Product p3 = new Product();
                p3.setTitle("Snake Game (Python)");
                p3.setDescription("I made Snake in Pygame. The code looks like spaghetti, please help!");
                p3.setCreatorUserId(1L); // admin
                if (gameDevCat != null) p3.setCategories(List.of(gameDevCat));

                Product p4 = new Product();
                p4.setTitle("E-commerce Dashboard");
                p4.setDescription("Full-stack React + Spring Boot dashboard. Need review on the API layer.");
                p4.setCreatorUserId(2L);
                if (frontendCat != null && backendCat != null) {
                    p4.setCategories(List.of(frontendCat, backendCat));
                }

                Product p5 = new Product();
                p5.setTitle("Weather App (React Native)");
                p5.setDescription("Mobile weather app using OpenWeatherMap API.");
                p5.setCreatorUserId(1L);

                Product p6 = new Product();
                p6.setTitle("Chat Application");
                p6.setDescription("Real-time chat app with WebSocket. Looking for architecture feedback.");
                p6.setCreatorUserId(2L);
                if (backendCat != null) p6.setCategories(List.of(backendCat));

                productRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6));

                // Add sample review comments to products
                Comment c1 = new Comment();
                c1.setDescription("Great code! But remember to hide your API keys in an .env file.");
                c1.setProduct(p1);
                c1.setCreatorUserId(1L);

                Comment c2 = new Comment();
                c2.setDescription("The DTO pattern looks correct! Consider adding validation annotations.");
                c2.setProduct(p2);
                c2.setCreatorUserId(1L);

                Comment c3 = new Comment();
                c3.setDescription("Nice project! Try separating game logic from rendering for cleaner code.");
                c3.setProduct(p3);
                c3.setCreatorUserId(2L);

                commentRepository.saveAll(List.of(c1, c2, c3));

                System.out.println("Database successfully seeded with test data!");
            }
        };
    }
}
