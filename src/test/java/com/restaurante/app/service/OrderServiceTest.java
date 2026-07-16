package com.restaurante.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.restaurante.app.models.Order;
import com.restaurante.app.models.OrderItem;
import com.restaurante.app.models.OrderItemDTO;
import com.restaurante.app.repository.OrderItemRepository;
import com.restaurante.app.repository.OrderRepository;
import com.restaurante.app.repository.OrderSummaryRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

/**
 * Pure unit test of {@link OrderService} with mocked repositories: verifies the business rules of
 * order creation (a new order enters as "pendiente" and its DTO line items are mapped to entities
 * bound to the saved order) without touching a database.
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderSummaryRepository orderSummaryRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_marksOrderPending_andPersistsMappedLineItems() {
        Order order = new Order();
        order.setUserId(2);
        order.setTableNumber(5);

        Order saved = new Order();
        saved.setId(42);
        when(orderRepository.save(any(Order.class))).thenReturn(saved);

        List<OrderItemDTO> items = List.of(
                new OrderItemDTO(1, 2, "Sin pepino"),
                new OrderItemDTO(3, 1, null));

        orderService.createOrder(order, items);

        // The header is saved as pending with creation timestamps set.
        assertThat(order.getStatus()).isEqualTo("pendiente");
        assertThat(order.getCreatedAt()).isNotNull();
        assertThat(order.getUpdatedAt()).isNotNull();

        // The DTOs are mapped to entities bound to the saved order's id.
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<OrderItem>> captor = ArgumentCaptor.forClass(List.class);
        verify(orderItemRepository).saveAll(captor.capture());

        List<OrderItem> persisted = captor.getValue();
        assertThat(persisted).hasSize(2);
        assertThat(persisted).allSatisfy(item -> assertThat(item.getOrderId()).isEqualTo(42));
        assertThat(persisted).extracting(OrderItem::getProductId).containsExactly(1, 3);
        assertThat(persisted).extracting(OrderItem::getQuantity).containsExactly(2, 1);
    }
}
