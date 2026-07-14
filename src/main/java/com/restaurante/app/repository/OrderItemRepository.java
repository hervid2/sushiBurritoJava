package com.restaurante.app.repository;

import com.restaurante.app.models.OrderItem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data repository for {@link OrderItem order line items}.
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    /**
     * @param orderId the owning order id
     * @return the line items belonging to the order
     */
    List<OrderItem> findByOrderId(Integer orderId);

    /**
     * Removes every line item of the given order (used when an order's lines are rebuilt).
     *
     * @param orderId the owning order id
     */
    void deleteByOrderId(Integer orderId);
}
