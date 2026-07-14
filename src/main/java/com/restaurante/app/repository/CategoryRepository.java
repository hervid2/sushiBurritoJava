package com.restaurante.app.repository;

import com.restaurante.app.models.Category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for menu {@link Category categories}.
 */
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    /**
     * @return every category ordered alphabetically by name (for combo boxes)
     */
    List<Category> findAllByOrderByNameAsc();

    /**
     * @param name the category name
     * @return the matching category, if any
     */
    Optional<Category> findByName(String name);
}
