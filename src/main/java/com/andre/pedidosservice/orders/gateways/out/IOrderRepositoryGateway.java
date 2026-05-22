package com.andre.pedidosservice.orders.gateways.out;

import com.andre.pedidosservice.orders.core.domain.OrderDomain;
import com.andre.pedidosservice.orders.core.domain.OrderItemDomain;

import java.util.List;
import java.util.Optional;

public interface IOrderRepositoryGateway {

    Optional<OrderDomain> getOrderById(String id);

    Optional<OrderItemDomain> getOrderItemById(String id);

    OrderDomain saveOrder(OrderDomain domain);

    OrderDomain saveOrderItem(String id, List<OrderItemDomain> itemDomains);

    void removeOrderById(String id);

}
