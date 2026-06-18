package com.andre.pedidosservice.notification.adapters.out;

import com.andre.pedidosservice.notification.adapters.config.RabbitMQConfig;
import com.andre.pedidosservice.notification.dtos.OrderNotificationEvent;
import com.andre.pedidosservice.order.gateways.out.OrderNotificationGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

// Adapter de saída: implementa o contrato definido em OrderNotificationGateway usando RabbitMQ como infraestrutura
// O OrderService não conhece esta classe — ele depende apenas da interface (OrderNotificationGateway)
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderRabbitMQPublisher implements OrderNotificationGateway {

    // RabbitTemplate é o componente do Spring que faz a comunicação com o RabbitMQ
    // Ele usa o Jackson2JsonMessageConverter configurado em RabbitMQConfig para serializar o evento para JSON
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void notifyOrderCreated(OrderNotificationEvent event) {
        // convertAndSend serializa o evento para JSON e envia ao exchange com a routing key de criação
        // O exchange roteia a mensagem para a fila "queue.order.created" conforme o binding configurado
        log.info("notify Created: {}" , event);
        log.info("Teste funcionou");
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_CREATED,
                event
        );
    }

    @Override
    public void notifyOrderFinished(OrderNotificationEvent event) {
        // Mesmo fluxo, mas com a routing key de finalização
        // O exchange roteia para "queue.order.finished"
        log.info("notify Finished: {}" , event);
        log.info("Teste funcionou");
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_FINISHED,
                event
        );
    }
}
