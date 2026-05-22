package com.andre.pedidosservice.orders.core.service;

import com.andre.pedidosservice.exception.exceptions.InvalidRequestException;
import com.andre.pedidosservice.exception.exceptions.ResourceNotFoundException;
import com.andre.pedidosservice.orders.core.OrderStatus;
import com.andre.pedidosservice.orders.core.domain.OrderDomain;
import com.andre.pedidosservice.orders.core.domain.OrderItemDomain;
import com.andre.pedidosservice.orders.gateways.in.IOrderGatewayService;
import com.andre.pedidosservice.orders.gateways.out.IOrderRepositoryGateway;

import java.util.List;

public class OrderService implements IOrderGatewayService {

    private final IOrderRepositoryGateway repository;

    public OrderService(IOrderRepositoryGateway repository) {
        this.repository = repository;
    }

    @Override
    public OrderDomain createOrder(OrderDomain domain) {
        if (repository.getOrderById(domain.getId()).isPresent()) {
            throw new InvalidRequestException("Pedido já existe");
        };

        return repository.saveOrder(domain);
    }

    @Override
    public OrderDomain addOrderItem(String id, List<OrderItemDomain> orderItemDomain) {
        return repository.saveOrderItem(id, orderItemDomain);
    }

    @Override
    public OrderDomain deleteOrderItem(String idOrder, String idOrderItem) {
        OrderDomain orderDomain = getOrderByID(idOrder);

        OrderItemDomain orderItemDomain = repository.getOrderItemById(idOrder).orElseThrow(
                () -> new ResourceNotFoundException("Item de pedido não encontrado"));

        if (!orderDomain.getOrderItemDomain().contains(orderItemDomain)) {
            throw new ResourceNotFoundException("Item de pedido não encontrado");
        }

        orderDomain.getOrderItemDomain().remove(orderItemDomain);

        return orderDomain;
    }


    @Override
    public OrderDomain updateStatusOrder(OrderStatus status, String id) {
        OrderDomain domain = getOrderByID(id);
        domain.setStatus(status);
        return repository.saveOrder(domain);
    }

    @Override
    public OrderDomain getOrderByID(String id) {
        return repository.getOrderById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Pedido não encontrado"));
    }

    @Override
    public void deleteOrder(String id) {
        getOrderByID(id);

        repository.removeOrderById(id);
    }

}
