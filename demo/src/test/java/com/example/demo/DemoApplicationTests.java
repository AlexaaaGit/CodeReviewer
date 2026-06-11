package com.example.demo;

import com.example.demo.dto.ProductRequest;
import com.example.demo.model.ReviewStatus;
import com.example.demo.model.SubmissionType;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.ProductService;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:devboard_test;DB_CLOSE_DELAY=-1",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.jpa.show-sql=false"
})
class DemoApplicationTests {

	@Autowired
	private ProductService productService;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private Validator validator;

	@Test
	void contextLoads() {
	}

	@Test
	void filtersProductsByReviewStatus() {
		var reviewed = productService.getProducts(null, null, ReviewStatus.REVIEWED, PageRequest.of(0, 100));
		var needsReview = productService.getProducts(null, null, ReviewStatus.NEEDS_REVIEW, PageRequest.of(0, 100));
		var all = productService.getProducts(null, null, ReviewStatus.ALL, PageRequest.of(0, 100));

		assertThat(reviewed.getContent()).isNotEmpty().allMatch(product -> product.commentCount() > 0);
		assertThat(needsReview.getContent()).isNotEmpty().allMatch(product -> product.commentCount() == 0);
		assertThat(reviewed.getTotalElements() + needsReview.getTotalElements())
				.isEqualTo(all.getTotalElements());
	}

	@Test
	void combinesTitleCategoryAndReviewStatusFilters() {
		Long backendCategoryId = categoryRepository.findAll().stream()
				.filter(category -> category.getName().equals("Backend"))
				.findFirst()
				.orElseThrow()
				.getId();

		var result = productService.getProducts(
				"API",
				backendCategoryId,
				ReviewStatus.REVIEWED,
				PageRequest.of(0, 100));

		assertThat(result.getContent()).isNotEmpty().allSatisfy(product -> {
			assertThat(product.title()).containsIgnoringCase("API");
			assertThat(product.commentCount()).isPositive();
			assertThat(product.categories()).anyMatch(category -> category.id().equals(backendCategoryId));
		});
	}

	@Test
	void rejectsBlankTitleAndMissingPastedCode() {
		ProductRequest request = new ProductRequest(
				"   ",
				"description",
				null,
				"   ",
				SubmissionType.PASTE,
				null,
				List.of());

		assertThat(validator.validate(request))
				.extracting(violation -> violation.getMessage())
				.contains("Title is required", "Code or a valid HTTP/HTTPS source URL is required");
	}

	@Test
	void rejectsInvalidSourceUrl() {
		ProductRequest request = new ProductRequest(
				"Valid title",
				"description",
				null,
				null,
				SubmissionType.GITHUB,
				"not-a-url",
				List.of());

		assertThat(validator.validate(request))
				.extracting(violation -> violation.getMessage())
				.contains("Code or a valid HTTP/HTTPS source URL is required");
	}
}
