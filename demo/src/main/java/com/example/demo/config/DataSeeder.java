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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
            User mentorTwoUser = ensureDemoUser(userRepository, passwordEncoder, "leadmentor", "mentor456", Role.ROLE_MENTOR, false);

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

            List<Category> allCategories = categoryRepository.findAll();
            Category frontendCat = allCategories.stream().filter(c -> c.getName().equals("Frontend")).findFirst().orElse(null);
            Category backendCat = allCategories.stream().filter(c -> c.getName().equals("Backend")).findFirst().orElse(null);
            Category mobileCat = allCategories.stream().filter(c -> c.getName().equals("Mobile")).findFirst().orElse(null);
            Category gameDevCat = allCategories.stream().filter(c -> c.getName().equals("Game Dev")).findFirst().orElse(null);
            Category devOpsCat = allCategories.stream().filter(c -> c.getName().equals("DevOps")).findFirst().orElse(null);

            seedCoreProjects(productRepository, commentRepository, adminUser, juniorUser, mentorUser, frontendCat, backendCat, mobileCat, gameDevCat);
            seedExtendedProjects(productRepository, commentRepository, adminUser, juniorUser, mentorUser, mentorTwoUser, frontendCat, backendCat, mobileCat, gameDevCat, devOpsCat);
            repairDemoData(productRepository, commentRepository, adminUser, juniorUser, mentorUser, mentorTwoUser);
        };
    }

    private void repairDemoData(
            ProductRepository productRepository,
            CommentRepository commentRepository,
            User adminUser,
            User juniorUser,
            User mentorUser,
            User mentorTwoUser) {

        productRepository.findAll().forEach(product -> {
            switch (product.getTitle()) {
                case "Netflix Clone (React)" -> {
                    product.setCreatorUserId(juniorUser.getId());
                    product.setSubmissionType(SubmissionType.PASTE);
                    product.setSourceUrl(null);
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
                    product.setSubmissionType(SubmissionType.GITHUB);
                    product.setSourceUrl("https://github.com/spring-projects/spring-petclinic");
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
                    product.setSubmissionType(SubmissionType.FILE);
                    product.setSourceUrl("https://github.com/pygame/pygame/blob/main/examples/chimp.py");
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
                    product.setSubmissionType(SubmissionType.PASTE);
                    product.setSourceUrl(null);
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
                    product.setSubmissionType(SubmissionType.GITHUB);
                    product.setSourceUrl("https://github.com/expo/examples");
                    if (isBlank(product.getCodeSnippet())) {
                        product.setCodeSnippet("""
                                const weather = await fetch(`${API_URL}?city=${city}&appid=${apiKey}`);
                                setForecast(await weather.json());
                                """);
                    }
                }
                case "Chat Application" -> {
                    product.setCreatorUserId(juniorUser.getId());
                    product.setSubmissionType(SubmissionType.GITHUB);
                    product.setSourceUrl("https://github.com/socketio/chat-example");
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

    private void seedCoreProjects(
            ProductRepository productRepository,
            CommentRepository commentRepository,
            User adminUser,
            User juniorUser,
            User mentorUser,
            Category frontendCat,
            Category backendCat,
            Category mobileCat,
            Category gameDevCat) {

        if (productRepository.count() > 0) {
            return;
        }

        System.out.println("Database is empty. Starting automatic seeding...");

        Product p1 = createProduct("Netflix Clone (React)", "I built a Netflix clone. Please review the login module.",
                "const handleLogin = async () => {\n  const response = await api.post('/login', { email, password });\n  localStorage.setItem('token', response.data.token);\n};",
                SubmissionType.PASTE, null, juniorUser.getId(), frontendCat);
        Product p2 = createProduct("Spring Boot Bank API", "My first REST API. Am I using the DTO pattern correctly?",
                "@PostMapping(\"/accounts\")\npublic Account create(@RequestBody Account account) {\n    return accountRepository.save(account);\n}",
                SubmissionType.PASTE, null, juniorUser.getId(), backendCat);
        Product p3 = createProduct("Snake Game (Python)", "I made Snake in Pygame. The code looks like spaghetti, please help!",
                "while running:\n    move_snake()\n    check_collision()\n    draw_everything()",
                SubmissionType.FILE, "https://github.com/pygame/pygame/blob/main/examples/chimp.py", adminUser.getId(), gameDevCat);
        Product p4 = createProduct("E-commerce Dashboard", "Full-stack React + Spring Boot dashboard. Need review on the API layer.",
                "useEffect(() => {\n  fetch('/api/orders').then(res => res.json()).then(setOrders);\n}, []);",
                SubmissionType.PASTE, null, juniorUser.getId(), frontendCat, backendCat);
        Product p5 = createProduct("Weather App (React Native)", "Mobile weather app using OpenWeatherMap API.",
                "const weather = await fetch(`${API_URL}?city=${city}&appid=${apiKey}`);\nsetForecast(await weather.json());",
                SubmissionType.GITHUB, "https://github.com/expo/examples", adminUser.getId(), frontendCat, mobileCat);
        Product p6 = createProduct("Chat Application", "Real-time chat app with WebSocket. Looking for architecture feedback.",
                "socket.onmessage = (event) => {\n  setMessages((items) => [...items, JSON.parse(event.data)]);\n};",
                SubmissionType.GITHUB, "https://github.com/socketio/chat-example", juniorUser.getId(), backendCat);

        productRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6));

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
    }

    private void seedExtendedProjects(
            ProductRepository productRepository,
            CommentRepository commentRepository,
            User adminUser,
            User juniorUser,
            User mentorUser,
            User mentorTwoUser,
            Category frontendCat,
            Category backendCat,
            Category mobileCat,
            Category gameDevCat,
            Category devOpsCat) {

        ensureProduct(productRepository, commentRepository, "Portfolio Site",
                "A personal portfolio with animations and responsive layout.",
                "const sections = ['hero', 'projects', 'contact'];",
                SubmissionType.PASTE, null, juniorUser.getId(), mentorUser.getId(), "Good layout structure, but split animations into reusable components.",
                frontendCat);

        ensureProduct(productRepository, commentRepository, "Task Manager API",
                "REST API for a task tracker with token auth.",
                "@GetMapping('/tasks')\npublic List<TaskDto> getTasks() { return taskService.getAll(); }",
                SubmissionType.GITHUB, "https://github.com/tastejs/todomvc", juniorUser.getId(), mentorTwoUser.getId(), "Looks solid; add tests for filtering and soft delete.",
                backendCat);

        ensureProduct(productRepository, commentRepository, "Learning Platform Dashboard",
                "Admin panel for a mentoring platform.",
                "fetch('/api/admin/stats').then(res => res.json()).then(setStats);",
                SubmissionType.FILE, "https://github.com/react/react/blob/main/packages/react/src/jsx/ReactJSX.js", adminUser.getId(), mentorUser.getId(), "The dashboard is clear, but consider breaking the charts into smaller cards.",
                frontendCat, backendCat);

        ensureProduct(productRepository, commentRepository, "Mobile Study Planner",
                "A cross-platform planner for students.",
                "await Notifications.scheduleNotificationAsync({ content, trigger });",
                SubmissionType.GITHUB, "https://github.com/expo/examples", juniorUser.getId(), mentorTwoUser.getId(), "Nice mobile flow. Watch for async state updates.",
                mobileCat);

        ensureProduct(productRepository, commentRepository, "Dockerized CI Pipeline",
                "Repository with Docker, GitHub Actions and deploy scripts.",
                "steps:\n  - name: Build\n    run: mvn test",
                SubmissionType.FILE, "https://raw.githubusercontent.com/docker/awesome-compose/master/README.md", adminUser.getId(), mentorUser.getId(), "Great start. Add caching and a lint stage.",
                devOpsCat);

        ensureProduct(productRepository, commentRepository, "Game Lobby Server",
                "Backend for a multiplayer game lobby.",
                "websocket.on('join', () => broadcastLobby());",
                SubmissionType.PASTE, null, juniorUser.getId(), mentorTwoUser.getId(), "This is easy to follow. Add rate limiting to join events.",
                gameDevCat, backendCat);

        ensureProduct(productRepository, commentRepository, "Mentor Review Starter",
                "Tiny project specifically for mentor reviews.",
                "function summarizeReview(score) {\n  return score >= 4 ? 'approved' : 'needs changes';\n}",
                SubmissionType.PASTE, null, juniorUser.getId(), mentorUser.getId(), "Useful example; include edge cases and validation.",
                frontendCat);

        ensureProduct(productRepository, commentRepository, "Blog Engine Admin",
                "Moderation tool for blog posts and comments.",
                "db.posts.updateMany({ status: 'pending' }, { $set: { reviewed: true } });",
                SubmissionType.GITHUB, "https://github.com/strapi/strapi", adminUser.getId(), mentorTwoUser.getId(), "The moderation flow is practical. Rename the action labels for clarity.",
                backendCat);

        ensureProduct(productRepository, commentRepository, "Realtime Notification Hub",
                "Push notifications dashboard for teams.",
                "socket.emit('notify', { userId, message });",
                SubmissionType.PASTE, null, mentorUser.getId(), mentorTwoUser.getId(), "Good separation of responsibilities, but extract notification formatting.",
                backendCat);

        ensureProduct(productRepository, commentRepository, "Code Review Checklist",
                "Simple checklist app for juniors before submission.",
                "const checklist = ['build', 'tests', 'docs', 'screenshots'];",
                SubmissionType.FILE, "https://raw.githubusercontent.com/github/gitignore/main/Java.gitignore", juniorUser.getId(), mentorUser.getId(), "Very useful as a pre-submit step. Maybe add warnings for missing fields.",
                frontendCat);
    }

    private void ensureProduct(
            ProductRepository productRepository,
            CommentRepository commentRepository,
            String title,
            String description,
            String codeSnippet,
            SubmissionType submissionType,
            String sourceUrl,
            Long creatorUserId,
            Long reviewAuthorUserId,
            String reviewText,
            Category... categories) {

        Product product = productRepository.findAll().stream()
                .filter(p -> p.getTitle().equals(title))
                .findFirst()
                .orElseGet(Product::new);

        boolean isNew = product.getId() == null;
        product.setTitle(title);
        product.setDescription(description);
        product.setCodeSnippet(codeSnippet);
        product.setSubmissionType(submissionType);
        product.setSourceUrl(sourceUrl);
        product.setCreatorUserId(creatorUserId);
        product.setCategories(filterCategories(categories));
        Product saved = productRepository.save(product);

        if (isNew || commentRepository.findAll().stream().noneMatch(c -> c.getProduct().getId().equals(saved.getId()) && c.getDescription().equals(reviewText))) {
            Comment comment = new Comment();
            comment.setDescription(reviewText);
            comment.setProduct(saved);
            comment.setCreatorUserId(reviewAuthorUserId);
            commentRepository.save(comment);
        }
    }

    private Product createProduct(
            String title,
            String description,
            String codeSnippet,
            SubmissionType submissionType,
            String sourceUrl,
            Long creatorUserId,
            Category... categories) {
        Product product = new Product();
        product.setTitle(title);
        product.setDescription(description);
        product.setCodeSnippet(codeSnippet);
        product.setSubmissionType(submissionType);
        product.setSourceUrl(sourceUrl);
        product.setCreatorUserId(creatorUserId);
        product.setCategories(filterCategories(categories));
        return product;
    }

    private List<Category> filterCategories(Category... categories) {
        return Arrays.stream(categories)
                .filter(Objects::nonNull)
                .toList();
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
