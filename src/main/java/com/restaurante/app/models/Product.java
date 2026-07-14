package com.restaurante.app.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A product on the menu.
 *
 * <p>Mapped to the {@code products} table. The owning category is kept as a plain foreign-key column
 * ({@link #categoryId}) rather than an association, matching how the views pass category ids around.
 * Column names map 1:1 to the fields (snake_case), so no {@code @Column} overrides are needed.
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String ingredients;

    private double netPrice;

    private double salePrice;

    private double tax;

    private Integer categoryId;

    public Product() {
    }

    public Product(Integer id, String name, String ingredients, double netPrice, double salePrice,
                   double tax, Integer categoryId) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.netPrice = netPrice;
        this.salePrice = salePrice;
        this.tax = tax;
        this.categoryId = categoryId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public double getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(double netPrice) {
        this.netPrice = netPrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Shown directly in {@code JComboBox<Product>}, so the drop-down displays the product name.
     */
    @Override
    public String toString() {
        return name;
    }
}
