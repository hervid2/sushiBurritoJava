package com.restaurante.app.service;

import com.restaurante.app.models.Category;
import com.restaurante.app.models.Order;
import com.restaurante.app.models.OrderItem;
import com.restaurante.app.models.OrderItemDTO;
import com.restaurante.app.models.Product;
import com.restaurante.app.repository.CategoryRepository;
import com.restaurante.app.repository.OrderItemRepository;
import com.restaurante.app.repository.OrderRepository;
import com.restaurante.app.repository.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Business rules for orders: creation, editing, status changes and the kitchen queue.
 *
 * <p>Free of Swing dependencies; persistence goes through Spring Data repositories.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                        ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Creates a new order together with its line items, marking it as pending so it reaches the
     * kitchen queue.
     *
     * @param order           the order header (user, table)
     * @param items           the line items entered in the view
     * @param productSummary  denormalised product summary stored on the order
     * @param categorySummary denormalised category summary stored on the order
     */
    @Transactional
    public void createOrder(Order order, List<OrderItemDTO> items,
                            String productSummary, String categorySummary) {
        LocalDateTime now = LocalDateTime.now();
        order.setStatus("pendiente");
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        order.setEntryTime(now);
        order.setProductSummary(productSummary);
        order.setCategorySummary(categorySummary);

        Order saved = orderRepository.save(order);
        orderItemRepository.saveAll(toOrderItems(saved.getId(), items));
    }

    /**
     * Updates an existing order and its line items, rebuilding the product/category summaries from the
     * supplied lines. Only the editable fields are touched, preserving the original creation metadata.
     *
     * @param order the order header carrying its id and edited values (table, status)
     * @param items the new line items that replace the previous ones
     */
    @Transactional
    public void updateOrder(Order order, List<OrderItemDTO> items) {
        StringBuilder productSummary = new StringBuilder();
        Set<String> uniqueCategories = new LinkedHashSet<>();
        for (OrderItemDTO item : items) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product != null) {
                if (productSummary.length() > 0) {
                    productSummary.append(", ");
                }
                productSummary.append(item.getQuantity()).append(" ").append(product.getName());

                if (product.getCategoryId() != null) {
                    categoryRepository.findById(product.getCategoryId())
                            .ifPresent(category -> uniqueCategories.add(category.getName()));
                }
            }
        }

        Order existing = orderRepository.findById(order.getId()).orElseThrow();
        existing.setTableNumber(order.getTableNumber());
        existing.setStatus(order.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setProductSummary(productSummary.toString());
        existing.setCategorySummary(String.join(", ", uniqueCategories));
        orderRepository.save(existing);

        orderItemRepository.deleteByOrderId(existing.getId());
        orderItemRepository.saveAll(toOrderItems(existing.getId(), items));
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
            Map<String, Object> row = new HashMap<>();
            row.put("id", order.getId());
            row.put("mesa", order.getTableNumber());
            row.put("productos", order.getProductSummary());
            row.put("categorias", order.getCategorySummary());

            LocalDateTime kitchenTime = order.getEntryTime();
            if (kitchenTime == null) {
                kitchenTime = order.getCreatedAt();
            }
            row.put("hora", kitchenTime);
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
