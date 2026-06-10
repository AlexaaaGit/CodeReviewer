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
            User adminUser = ensureDemoUser(userRepository, passwordEncoder, "admin", "admin123", Role.ROLE_ADMIN, true);
            User juniorUser = ensureDemoUser(userRepository, passwordEncoder, "user", "user123", Role.ROLE_JUNIOR, false);
            User mentorUser = ensureDemoUser(userRepository, passwordEncoder, "mentor", "mentor123", Role.ROLE_MENTOR, false);

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
                p1.setCodeSnippet("""
                        const handleLogin = async () => {
                          const response = await api.post('/login', { email, password });
                          localStorage.setItem('token', response.data.token);
                        };
                        """);
                p1.setCreatorUserId(juniorUser.getId());
                if (frontendCat != null) p1.setCategories(List.of(frontendCat));

                Product p2 = new Product();
                p2.setTitle("Spring Boot Bank API");
                p2.setDescription("My first REST API. Am I using the DTO pattern correctly?");
                p2.setCodeSnippet("""
                        @PostMapping("/accounts")
                        public Account create(@RequestBody Account account) {
                            return accountRepository.save(account);
                        }
                        """);
                p2.setCreatorUserId(juniorUser.getId());
                if (backendCat != null) p2.setCategories(List.of(backendCat));

                Product p3 = new Product();
                p3.setTitle("Snake Game (Python)");
                p3.setDescription("I made Snake in Pygame. The code looks like spaghetti, please help!");
                p3.setCodeSnippet("""
                        while running:
                            move_snake()
                            check_collision()
                            draw_everything()
                        """);
                p3.setCreatorUserId(adminUser.getId());
                if (gameDevCat != null) p3.setCategories(List.of(gameDevCat));

                Product p4 = new Product();
                p4.setTitle("E-commerce Dashboard");
                p4.setDescription("Full-stack React + Spring Boot dashboard. Need review on the API layer.");
                p4.setCodeSnippet("""
                        useEffect(() => {
                          fetch('/api/orders').then(res => res.json()).then(setOrders);
                        }, []);
                        """);
                p4.setCreatorUserId(juniorUser.getId());
                if (frontendCat != null && backendCat != null) {
                    p4.setCategories(List.of(frontendCat, backendCat));
                }

                Product p5 = new Product();
                p5.setTitle("Weather App (React Native)");
                p5.setDescription("Mobile weather app using OpenWeatherMap API.");
                p5.setCodeSnippet("""
                        const weather = await fetch(`${API_URL}?city=${city}&appid=${apiKey}`);
                        setForecast(await weather.json());
                        """);
                p5.setCreatorUserId(adminUser.getId());

                Product p6 = new Product();
                p6.setTitle("Chat Application");
                p6.setDescription("Real-time chat app with WebSocket. Looking for architecture feedback.");
                p6.setCodeSnippet("""
                        socket.onmessage = (event) => {
                          setMessages((items) => [...items, JSON.parse(event.data)]);
                        };
                        """);
                p6.setCreatorUserId(juniorUser.getId());
                if (backendCat != null) p6.setCategories(List.of(backendCat));

                productRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6));

                // Add sample review comments to products
                Comment c1 = new Comment();
                c1.setDescription("Great code! But remember to hide your API keys in an .env file.");
                c1.setProduct(p1);
                c1.setCreatorUserId(mentorUser.getId());

                Comment c2 = new Comment();
                c2.setDescription("The DTO pattern looks correct! Consider adding validation annotations.");
                c2.setProduct(p2);
                c2.setCreatorUserId(mentorUser.getId());

                Comment c3 = new Comment();
                c3.setDescription("Nice project! Try separating game logic from rendering for cleaner code.");
                c3.setProduct(p3);
                c3.setCreatorUserId(mentorUser.getId());

                commentRepository.saveAll(List.of(c1, c2, c3));

                System.out.println("Database successfully seeded with test data!");
            }

            repairDemoData(productRepository, commentRepository, adminUser, juniorUser, mentorUser);
        };
    }

    private void repairDemoData(
            ProductRepository productRepository,
            CommentRepository commentRepository,
            User adminUser,
            User juniorUser,
            User mentorUser) {

        productRepository.findAll().forEach(product -> {
            switch (product.getTitle()) {
                case "Netflix Clone (React)" -> {
                    product.setCreatorUserId(juniorUser.getId());
                    if (isBlank(product.getCodeSnippet())) {
                        product.setCodeSnippet("""
                                const handleLogin = async () => {
                                  const response = await api.post('/login', { email, password });
                                  localStorage.setItem('token', response.data.token);
                                };
                                """);
                    }
                }
                case "Spring Boot Bank API" -> {
                    product.setCreatorUserId(juniorUser.getId());
                    if (isBlank(product.getCodeSnippet())) {
                        product.setCodeSnippet("""
                                @PostMapping("/accounts")
                                public Account create(@RequestBody Account account) {
                                    return accountRepository.save(account);
                                }
                                """);
                    }
                }
                case "Snake Game (Python)" -> {
                    product.setCreatorUserId(adminUser.getId());
                    if (isBlank(product.getCodeSnippet())) {
                        product.setCodeSnippet("""
                                while running:
                                    move_snake()
                                    check_collision()
                                    draw_everything()
                                """);
                    }
                }
                case "E-commerce Dashboard" -> {
                    product.setCreatorUserId(juniorUser.getId());
                    if (isBlank(product.getCodeSnippet())) {
                        product.setCodeSnippet("""
                                useEffect(() => {
                                  fetch('/api/orders').then(res => res.json()).then(setOrders);
                                }, []);
                                """);
                    }
                }
                case "Weather App (React Native)" -> {
                    product.setCreatorUserId(adminUser.getId());
                    if (isBlank(product.getCodeSnippet())) {
                        product.setCodeSnippet("""
                                const weather = await fetch(`${API_URL}?city=${city}&appid=${apiKey}`);
                                setForecast(await weather.json());
                                """);
                    }
                }
                case "Chat Application" -> {
                    product.setCreatorUserId(juniorUser.getId());
                    if (isBlank(product.getCodeSnippet())) {
                        product.setCodeSnippet("""
                                socket.onmessage = (event) => {
                                  setMessages((items) => [...items, JSON.parse(event.data)]);
                                };
                                """);
                    }
                }
                default -> {
                }
            }
            productRepository.save(product);
        });

        commentRepository.findAll().forEach(comment -> {
            if ("Nice project! Try separating game logic from rendering for cleaner code.".equals(comment.getDescription())) {
                comment.setCreatorUserId(mentorUser.getId());
                commentRepository.save(comment);
            }
        });
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private User ensureDemoUser(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            String username,
            String password,
            Role role,
            boolean resetCredentials) {

        User user = userRepository.findByUsername(username).orElseGet(() -> {
            User created = new User();
            created.setUsername(username);
            created.setPassword(passwordEncoder.encode(password));
            created.setRole(role);
            System.out.printf("Created demo user: %s/%s (%s)%n", username, password, role.name());
            return created;
        });

        if (resetCredentials) {
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
        }

        return userRepository.save(user);
    }
}
