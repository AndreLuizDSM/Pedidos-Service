package com.andre.pedidosservice.orders.adapters.out;

import com.andre.pedidosservice.orders.core.domain.OrderDomain;
import com.andre.pedidosservice.orders.core.domain.OrderItemDomain;
import com.andre.pedidosservice.orders.gateways.out.IOrderRepositoryGateway;
import com.andre.pedidosservice.orders.ports.out.IOrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements IOrderRepositoryGateway {

    private final IOrderJpaRepository jpaRepository;

    @Override
    public Optional<OrderDomain> getOrderById(String id) {

        return jpaRepository;
    }

    @Override
    public Optional<OrderItemDomain> getOrderItemById(String id) {

        return Optional.empty();
    }

    @Override
    public OrderDomain saveOrder(OrderDomain domain) {

        return domain;
    }

    @Override
    public OrderDomain saveOrderItem(String id, List<OrderItemDomain> itemDomains) {

        return null;
    }

    @Override
    public void removeOrderById(String id) {

    }
}
