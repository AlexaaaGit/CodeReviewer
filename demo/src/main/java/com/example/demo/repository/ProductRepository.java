package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(
            value = """
                    select p from Product p
                    where p.isDeleted = false
                      and (:title is null or lower(p.title) like lower(concat('%', :title, '%')))
                      and (:categoryId is null or exists (
                            select category.id from Product categoryProduct
                            join categoryProduct.categories category
                            where categoryProduct = p and category.id = :categoryId
                      ))
                      and (:reviewedOnly = false or exists (
                            select review.id from Comment review
                            where review.product = p and review.isDeleted = false
                      ))
                      and (:needsReviewOnly = false or not exists (
                            select review.id from Comment review
                            where review.product = p and review.isDeleted = false
                      ))
                    """,
            countQuery = """
                    select count(p) from Product p
                    where p.isDeleted = false
                      and (:title is null or lower(p.title) like lower(concat('%', :title, '%')))
                      and (:categoryId is null or exists (
                            select category.id from Product categoryProduct
                            join categoryProduct.categories category
                            where categoryProduct = p and category.id = :categoryId
                      ))
                      and (:reviewedOnly = false or exists (
                            select review.id from Comment review
                            where review.product = p and review.isDeleted = false
                      ))
                      and (:needsReviewOnly = false or not exists (
                            select review.id from Comment review
                            where review.product = p and review.isDeleted = false
                      ))
                    """
    )
    Page<Product> findFiltered(
            @Param("title") String title,
            @Param("categoryId") Long categoryId,
            @Param("reviewedOnly") boolean reviewedOnly,
            @Param("needsReviewOnly") boolean needsReviewOnly,
            Pageable pageable);

    // Find all non-deleted products by a specific creator (used in User Profile)
    List<Product> findByCreatorUserIdAndIsDeletedFalse(Long creatorUserId);
}
