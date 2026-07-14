package com.restaurante.app.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A product on the menu.
 *
 * <p>Mapped to the {@code productos} table. The owning category is kept as a plain foreign-key column
 * ({@link #categoryId}) rather than an association, matching how the views pass category ids around.
 * The {@code @Column} overrides map the still-Spanish schema and are dropped in Iteration 5.
 */
@Entity
@Table(name = "productos")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id")
    private Integer id;

    @Column(name = "nombre")
    private String name;

    @Column(name = "ingredientes")
    private String ingredients;

    @Column(name = "valor_neto")
    private double netPrice;

    @Column(name = "valor_venta")
    private double salePrice;

    @Column(name = "impuesto")
    private double tax;

    @Column(name = "categoria_id")
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
