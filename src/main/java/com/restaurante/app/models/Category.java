package com.restaurante.app.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A menu category (e.g. sushi, main dishes).
 *
 * <p>Mapped to the {@code categories} table; column names map 1:1 to the fields, so no
 * {@code @Column} overrides are needed after the Iteration 5 rename.
 */
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    public Category() {
    }

    public Category(Integer id, String name) {
        this.id = id;
        this.name = name;
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

    /**
     * Shown directly in {@code JComboBox<Category>}, so the drop-down displays the category name.
     */
    @Override
    public String toString() {
        return name;
    }
}
