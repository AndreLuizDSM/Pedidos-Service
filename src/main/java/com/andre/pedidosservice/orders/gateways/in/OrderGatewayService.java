package com.andre.pedidosservice.orders.gateways.in;

import com.andre.pedidosservice.orders.core.OrderStatus;
import com.andre.pedidosservice.orders.core.domain.OrderDomain;
import com.andre.pedidosservice.orders.core.domain.OrderItemDomain;

import java.util.List;

/*
 * Porta de entrada do módulo Orders.
 * É o contrato que o controller usa para se comunicar com a camada de serviço.
 */
public interface OrderGatewayService {

    OrderDomain createOrder(String userId);

    OrderDomain addOrderItem(String orderId, List<OrderItemDomain> items);

    OrderDomain updateOrderStatus(String id, OrderStatus status);

    OrderDomain getOrderById(String id);

    void deleteOrder(String id);

    OrderDomain deleteOrderItem(String orderId, String orderItemId);

    List<OrderDomain> getOrdersByUserId(String userId);
}
