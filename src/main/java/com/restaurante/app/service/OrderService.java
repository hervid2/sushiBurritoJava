package com.restaurante.app.service;

import com.restaurante.app.models.Order;
import com.restaurante.app.models.OrderItem;
import com.restaurante.app.models.OrderItemDTO;
import com.restaurante.app.models.OrderSummary;
import com.restaurante.app.repository.OrderItemRepository;
import com.restaurante.app.repository.OrderRepository;
import com.restaurante.app.repository.OrderSummaryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Business rules for orders: creation, editing, status changes and the kitchen queue.
 *
 * <p>Free of Swing dependencies; persistence goes through Spring Data repositories.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderSummaryRepository orderSummaryRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                        OrderSummaryRepository orderSummaryRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderSummaryRepository = orderSummaryRepository;
    }

    /**
     * Creates a new order together with its line items, marking it as pending so it reaches the
     * kitchen queue. The product/category summaries are no longer stored on the order; they are
     * derived from the line items by the {@code v_order_summary} view.
     *
     * @param order the order header (user, table)
     * @param items the line items entered in the view
     */
    @Transactional
    public void createOrder(Order order, List<OrderItemDTO> items) {
        LocalDateTime now = LocalDateTime.now();
        order.setStatus("pendiente");
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        Order saved = orderRepository.save(order);
        orderItemRepository.saveAll(toOrderItems(saved.getId(), items));
    }

    /**
     * Updates an existing order and its line items. Only the editable fields are touched, preserving
     * the original creation metadata; the product/category summaries follow automatically from the
     * replaced line items through the {@code v_order_summary} view.
     *
     * @param order the order header carrying its id and edited values (table, status)
     * @param items the new line items that replace the previous ones
     */
    @Transactional
    public void updateOrder(Order order, List<OrderItemDTO> items) {
        Order existing = orderRepository.findById(order.getId()).orElseThrow();
        existing.setTableNumber(order.getTableNumber());
        existing.setStatus(order.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(existing);

        orderItemRepository.deleteByOrderId(existing.getId());
        orderItemRepository.saveAll(toOrderItems(existing.getId(), items));
    }

    /**
     * Returns the human-readable product summary of an order (e.g. {@code "2 California Roll, 1 Té
     * Verde"}), rebuilt from its line items by the {@code v_order_summary} view.
     *
     * @param orderId the order id
     * @return the product summary, or an empty string if the order has no line items yet
     */
    public String getProductSummary(int orderId) {
        return orderSummaryRepository.findById(orderId)
                .map(OrderSummary::getProductSummary)
                .orElse("");
    }

    /**
     * Updates the status of an order.
     *
     * @param orderId   the order id
     * @param newStatus the new status (e.g. "cancelado", "entregado", "pagado")
     */
    @Transactional
    public void updateStatus(int orderId, String newStatus) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    /**
     * @param orderId the order id
     * @return the order, or {@code null} if not found
     */
    public Order findOrderById(int orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    /**
     * @param orderId the order id
     * @return the line items belonging to the order
     */
    public List<OrderItem> findItemsByOrderId(int orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    /**
     * @return every order, newest first
     */
    public List<Order> findAllOrders() {
        return orderRepository.findAllByOrderByIdDesc();
    }

    /**
     * @param status the status to filter by
     * @return orders in the given status
     */
    public List<Order> findOrdersByStatus(String status) {
        return orderRepository.findByStatusOrderByIdAsc(status);
    }

    /**
     * Builds the kitchen queue by combining pending and in-preparation orders and formatting each one
     * for the kitchen table.
     *
     * @return one map per order with keys {@code id}, {@code mesa}, {@code productos},
     *         {@code categorias}, {@code hora} and {@code estado}
     */
    public List<Map<String, Object>> getKitchenOrders() {
        List<Order> relevant = new ArrayList<>();
        relevant.addAll(orderRepository.findByStatusOrderByIdAsc("pendiente"));
        relevant.addAll(orderRepository.findByStatusOrderByIdAsc("preparando"));

        List<Map<String, Object>> formatted = new ArrayList<>();
        for (Order order : relevant) {
            OrderSummary summary = orderSummaryRepository.findById(order.getId()).orElse(null);

            Map<String, Object> row = new HashMap<>();
            row.put("id", order.getId());
            row.put("mesa", order.getTableNumber());
            row.put("productos", summary != null ? summary.getProductSummary() : "");
            row.put("categorias", summary != null ? summary.getCategorySummary() : "");
            row.put("hora", order.getCreatedAt());
            row.put("estado", order.getStatus());

            formatted.add(row);
        }
        return formatted;
    }

    /**
     * Maps the view-facing line-item DTOs to persistence entities bound to the given order.
     */
    private List<OrderItem> toOrderItems(Integer orderId, List<OrderItemDTO> items) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemDTO dto : items) {
            OrderItem item = new OrderItem();
            item.setOrderId(orderId);
            item.setProductId(dto.getProductId());
            item.setQuantity(dto.getQuantity());
            item.setNotes(dto.getNotes());
            orderItems.add(item);
        }
        return orderItems;
    }
}
