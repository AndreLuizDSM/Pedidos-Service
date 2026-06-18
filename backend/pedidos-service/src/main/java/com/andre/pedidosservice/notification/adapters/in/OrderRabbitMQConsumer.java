package com.andre.pedidosservice.notification.adapters.in;

import com.andre.pedidosservice.notification.adapters.config.RabbitMQConfig;
import com.andre.pedidosservice.notification.dtos.OrderNotificationEvent;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderRabbitMQConsumer {

    @RabbitListener(bindings = @QueueBinding(value = @Queue(RabbitMQConfig.QUEUE_ORDER_CREATED),
    exchange = @Exchange(RabbitMQConfig.EXCHANGE),
    key = RabbitMQConfig.ROUTING_KEY_CREATED))
    public void processMessage(final Message message, final OrderNotificationEvent event) {

        log.info("Prioridade {}" , message.getMessageProperties().getPriority());
        log.info("Evento: {}", event);
    }
}
