package com.andre.pedidosservice.orders.gateways.in;

import com.andre.pedidosservice.orders.core.OrderStatus;
import com.andre.pedidosservice.orders.core.domain.OrderDomain;
import com.andre.pedidosservice.orders.core.domain.OrderItemDomain;

import java.util.List;

/*
 * Porta de entrada do módulo Orders.
 * É o contrato que o controller usa para se comunicar com a camada de serviço.
 */
public interface IOrderGatewayService {

    OrderDomain createOrder(String userId);

    OrderDomain addOrderItem(String id, List<OrderItemDomain> orderItemDomain);

    OrderDomain updateStatusOrder(OrderStatus status, String id);

    OrderDomain getOrderByID(String id);

    void deleteOrder(String id);

    OrderDomain deleteOrderItemByID(String orderID, String orderItemID);

    List<OrderDomain> getOrdersByUserId(String userId);
}
