package com.andre.pedidosservice.order.core.service;

import com.andre.pedidosservice.order.core.OrderStatus;
import com.andre.pedidosservice.order.gateways.out.OrderRepositoryGateway;

public class OrderExpirationService implements com.andre.pedidosservice.order.gateways.in.OrderExpirationGatewayService {

    private final OrderRepositoryGateway repository;

    public OrderExpirationService(OrderRepositoryGateway repository) {
        this.repository = repository;
    }

    @Override
    public void expireOrders() {
        // findExpiredOrders retorna uma lista de Pedidos pendentes e que a hora atual passou a do Expirate
        // Abaixo faz a conversão de Pendente para EXPIRADO , individualmente

        repository.findExpiredOrders()
                .forEach(order -> repository.updateStatus(order.getId(), OrderStatus.EXPIRADO));
    }

    @Override
    public void deleteExpiredOrders() {
        // Deleta pelo status EXPIRADO
        repository.deleteByStatus(OrderStatus.EXPIRADO);
    }
}
