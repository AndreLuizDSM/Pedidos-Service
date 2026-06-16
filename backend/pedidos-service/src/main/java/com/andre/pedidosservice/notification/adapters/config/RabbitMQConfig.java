package com.andre.pedidosservice.notification.adapters.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "pedidos.exchange";

    public static final String QUEUE_ORDER_CREATED  = "queue.order.created";
    public static final String QUEUE_ORDER_FINISHED = "queue.order.finished";

    public static final String ROUTING_KEY_CREATED  = "order.created";
    public static final String ROUTING_KEY_FINISHED = "order.finished";

    @Bean
    public DirectExchange pedidosExchange() {
        return new DirectExchange(EXCHANGE);
    }

    // --- Filas ---

    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(QUEUE_ORDER_CREATED, true); // durable = true: fila sobrevive a restart do RabbitMQ
    }

    @Bean
    public Queue orderFinishedQueue() {
        return new Queue(QUEUE_ORDER_FINISHED, true);
    }

    // --- Bindings (liga fila ao exchange pela routing key) ---

    @Bean
    public Binding bindingOrderCreated(Queue orderCreatedQueue, DirectExchange pedidosExchange) {
        return BindingBuilder.bind(orderCreatedQueue).to(pedidosExchange).with(ROUTING_KEY_CREATED);
    }

    @Bean
    public Binding bindingOrderFinished(Queue orderFinishedQueue, DirectExchange pedidosExchange) {
        return BindingBuilder.bind(orderFinishedQueue).to(pedidosExchange).with(ROUTING_KEY_FINISHED);
    }

    // Converte objetos Java para JSON automaticamente ao publicar/consumir
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
