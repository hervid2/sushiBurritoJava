package com.restaurante.app.repository;

import com.restaurante.app.models.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data repository for {@link Product menu products}.
 */
public interface ProductRepository extends JpaRepository<Product, Integer> {

    /**
     * Products ordered by their category name and then their own name, matching the ordering the
     * combo boxes relied on. Uses an explicit join because the category is a plain foreign key.
     *
     * @return every product, grouped visually by category
     */
    @Query("SELECT p FROM Product p, Category c WHERE p.categoryId = c.id ORDER BY c.name, p.name")
    List<Product> findAllOrderedByCategoryName();
}
