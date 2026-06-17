package com.andre.pedidosservice.notification.dtos;

import com.andre.pedidosservice.order.core.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

// Representa o objeto que será serializado para JSON e enviado como mensagem ao RabbitMQ
// Contém apenas os dados relevantes do pedido para quem for consumir a notificação
@Data
@Builder
public class OrderNotificationEvent {

    // ID do pedido que gerou o evento
    private String orderId;

    // ID do usuário dono do pedido
    private String userId;

    // Status do pedido no momento do evento (ex: PENDENTE ao criar, ENTREGUE ao finalizar)
    private OrderStatus status;

    // Valor total do pedido no momento do evento
    private double totalAmount;

    // Momento exato em que o evento ocorreu
    private LocalDateTime occurredAt;
}
