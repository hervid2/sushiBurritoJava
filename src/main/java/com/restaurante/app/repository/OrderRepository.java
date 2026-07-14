package com.restaurante.app.repository;

import com.restaurante.app.models.Order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data repository for customer {@link Order orders}.
 */
public interface OrderRepository extends JpaRepository<Order, Integer> {

    /**
     * @param status the status to filter by (e.g. "pendiente", "preparando", "entregado")
     * @return orders in the given status, oldest first
     */
    List<Order> findByStatusOrderByIdAsc(String status);

    /**
     * @return every order, newest first
     */
    List<Order> findAllByOrderByIdDesc();
}
