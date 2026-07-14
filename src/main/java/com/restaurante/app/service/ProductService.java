package com.restaurante.app.service;

import com.restaurante.app.models.Category;
import com.restaurante.app.models.Product;
import com.restaurante.app.repository.CategoryRepository;
import com.restaurante.app.repository.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Business rules for products and their categories, used by the menu-management and order screens.
 *
 * <p>Free of Swing dependencies; persistence goes through Spring Data repositories.
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * @param product the product to create
     */
    @Transactional
    public void insert(Product product) {
        productRepository.save(product);
    }

    /**
     * @param product the product carrying updated data
     */
    @Transactional
    public void update(Product product) {
        productRepository.save(product);
    }

    /**
     * @param productId the id of the product to delete
     */
    @Transactional
    public void delete(int productId) {
        productRepository.deleteById(productId);
    }

    /**
     * @return every product, grouped visually by category name
     */
    public List<Product> findAll() {
        return productRepository.findAllOrderedByCategoryName();
    }

    /**
     * @param productId the product id
     * @return the product, or {@code null} if not found
     */
    public Product findProductById(int productId) {
        return productRepository.findById(productId).orElse(null);
    }

    /**
     * @return every category
     */
    public List<Category> findAllCategories() {
        return categoryRepository.findAllByOrderByNameAsc();
    }

    /**
     * @return the category names, alphabetically ordered, for populating combo boxes
     */
    public String[] getCategoryNames() {
        return categoryRepository.findAllByOrderByNameAsc().stream()
                .map(Category::getName)
                .toArray(String[]::new);
    }

    /**
     * @param categoryName the category name
     * @return the id of the category
     * @throws IllegalArgumentException if no category has that name
     */
    public int getCategoryIdByName(String categoryName) {
        return categoryRepository.findByName(categoryName)
                .map(Category::getId)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: " + categoryName));
    }

    /**
     * @param categoryId the category id
     * @return the category name, or a placeholder if not found
     */
    public String getCategoryNameById(int categoryId) {
        return categoryRepository.findById(categoryId)
                .map(Category::getName)
                .orElse("Desconocida");
    }

    /**
     * @return a map of category id to category name
     */
    public Map<Integer, String> getCategoryIdToNameMap() {
        Map<Integer, String> map = new LinkedHashMap<>();
        for (Category category : categoryRepository.findAll()) {
            map.put(category.getId(), category.getName());
        }
        return map;
    }

    /**
     * @return a map of category name to category id
     */
    public Map<String, Integer> getCategoryNameToIdMap() {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (Category category : categoryRepository.findAll()) {
            map.put(category.getName(), category.getId());
        }
        return map;
    }
}
