package com.andre.pedidosservice.orders.gateways.out;

import com.andre.pedidosservice.orders.core.OrderStatus;
import com.andre.pedidosservice.orders.core.domain.OrderDomain;
import com.andre.pedidosservice.orders.core.domain.OrderItemDomain;
import com.andre.pedidosservice.users.core.domain.UserDomain;

import java.util.List;
import java.util.Optional;

/*
 * Porta de saída do módulo Orders.
 * Define o contrato que o adapter de persistência deve implementar.
 * O OrderService depende apenas desta interface, nunca do JPA diretamente.
 */
public interface IOrderRepositoryGateway {

    Optional<OrderDomain> getOrderById(String id);

    Optional<OrderItemDomain> getOrderItemById(String itemId);

    OrderDomain createOrder(OrderDomain domain, UserDomain user);

    OrderDomain updateOrderStatus(String orderId, OrderStatus status);

    OrderDomain saveOrderItems(String orderId, List<OrderItemDomain> preparedItems, double newTotal);

    void removeOrderById(String id);

    void removeOrderItemById(String orderId, String itemId, double newTotal);

    List<OrderDomain> getOrdersByUserId(String userId);
}
