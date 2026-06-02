package com.andre.pedidosservice.orders.gateways.out;

import com.andre.pedidosservice.orders.core.OrderStatus;
import com.andre.pedidosservice.orders.core.domain.OrderDomain;
import com.andre.pedidosservice.orders.core.domain.OrderItemDomain;
import com.andre.pedidosservice.users.core.domain.UserDomain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepositoryGateway {

    Optional<OrderDomain> findById(String id);

    Optional<OrderItemDomain> findItemById(String itemId);

    OrderDomain save(OrderDomain domain, UserDomain user);

    OrderDomain updateStatus(String orderId, OrderStatus status);

    OrderDomain saveItems(OrderDomain order, List<OrderItemDomain> preparedItems, double newTotal);

    void deleteById(String id);

    void deleteItemById(String orderId, String itemId, double newTotal);

    List<OrderDomain> findByUserId(String userId);

    List<OrderDomain> findExpiredOrders();

    void deleteByStatus(OrderStatus status);

}
