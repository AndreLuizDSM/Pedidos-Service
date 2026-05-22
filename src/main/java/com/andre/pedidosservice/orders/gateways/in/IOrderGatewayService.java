package com.andre.pedidosservice.orders.gateways.in;

import com.andre.pedidosservice.orders.core.OrderStatus;
import com.andre.pedidosservice.orders.core.domain.OrderDomain;
import com.andre.pedidosservice.orders.core.domain.OrderItemDomain;
import org.hibernate.query.Order;

import java.util.List;

public interface IOrderGatewayService {

    OrderDomain createOrder(OrderDomain domain);

    OrderDomain addOrderItem(String id, List<OrderItemDomain> orderItemDomain);

    OrderDomain deleteOrderItem(String idOrder, String idOrderItem);

    OrderDomain updateStatusOrder(OrderStatus status, String id);

    OrderDomain getOrderByID(String id);

    void deleteOrder(String id);

}
