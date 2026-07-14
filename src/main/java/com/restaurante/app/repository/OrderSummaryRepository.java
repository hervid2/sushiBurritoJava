package com.restaurante.app.repository;

import com.restaurante.app.models.OrderSummary;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Read-only Spring Data repository over the {@code v_order_summary} view. Used to fetch the product
 * and category summaries that were previously stored as denormalised columns on {@code orders}.
 */
public interface OrderSummaryRepository extends JpaRepository<OrderSummary, Integer> {
}
