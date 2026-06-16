package com.andre.pedidosservice.order.adapters.in;

import com.andre.pedidosservice.order.gateways.in.OrderExpirationGatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderExpirationScheduler {

    private final OrderExpirationGatewayService expirationGateway;

    // A cada 10 minutos
    @Scheduled(cron = "${cron.horario}")
    public void checkExpiredOrders() {
        // Faz a expiração dos pedidos
        expirationGateway.expireOrders();
        // Faz o delete dos pedidos
        expirationGateway.deleteExpiredOrders();
    }
}
