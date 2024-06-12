package com.example.demo.repositories;

import com.example.demo.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

    @Query("SELECT p FROM Product p WHERE LOWER(p.category.name) LIKE LOWER(CONCAT('%', :category, '%'))")
    List<Product> findByCategoryNameContainingIgnoreCase(@Param("category") String category);

    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.category.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Product> findByNameOrDescriptionOrCategoryNameContainingIgnoreCase(@Param("searchTerm") String searchTerm);
}
