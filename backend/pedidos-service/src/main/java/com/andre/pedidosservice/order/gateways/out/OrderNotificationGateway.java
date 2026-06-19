package com.andre.pedidosservice.order.gateways.out;

import com.andre.pedidosservice.notification.dtos.OrderNotificationEvent;

// Porta de saída do domínio de pedidos para o sistema de notificação.
// O OrderService depende apenas desta interface — não sabe nem se importa que a implementação usa RabbitMQ.
// Isso mantém o core isolado de detalhes de infraestrutura (princípio da inversão de dependência)
public interface OrderNotificationGateway {

    // Publica um evento informando que um pedido foi criado
    void notifyOrderCreated(OrderNotificationEvent event);

    // Publica um evento informando que um pedido foi finalizado (status ENTREGUE)
    void notifyOrderFinished(OrderNotificationEvent event);
}
