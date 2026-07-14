package com.restaurante.app.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A menu category (e.g. sushi, main dishes).
 *
 * <p>Mapped to the {@code categorias} table; the {@code @Column} overrides map the still-Spanish
 * columns and are dropped once Iteration 5 renames the schema.
 */
@Entity
@Table(name = "categorias")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoria_id")
    private Integer id;

    @Column(name = "nombre")
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
