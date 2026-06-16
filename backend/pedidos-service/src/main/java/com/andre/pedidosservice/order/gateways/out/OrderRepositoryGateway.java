package com.andre.pedidosservice.order.gateways.out;

import com.andre.pedidosservice.order.core.OrderStatus;
import com.andre.pedidosservice.order.core.domain.OrderDomain;
import com.andre.pedidosservice.order.core.domain.OrderItemDomain;
import com.andre.pedidosservice.user.core.domain.UserDomain;

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
