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

    // Nome do Exchange — ponto central que recebe todas as mensagens e as roteia para as filas certas
    public static final String EXCHANGE = "pedidos.exchange";

    // Nomes das filas — cada fila armazena mensagens de um evento específico até o consumer consumi-las
    public static final String QUEUE_ORDER_CREATED  = "queue.order.created";
    public static final String QUEUE_ORDER_FINISHED = "queue.order.finished";

    // Routing keys — "etiquetas" que o producer coloca na mensagem para o Exchange saber para qual fila enviar
    public static final String ROUTING_KEY_CREATED  = "order.created";
    public static final String ROUTING_KEY_FINISHED = "order.finished";

    // DirectExchange roteia a mensagem para a fila cuja routing key é exatamente igual à da mensagem enviada
    @Bean
    public DirectExchange pedidosExchange() {
        return new DirectExchange(EXCHANGE);
    }

    // durable = true garante que a fila não seja perdida se o RabbitMQ reiniciar
    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(QUEUE_ORDER_CREATED, true);
    }

    @Bean
    public Queue orderFinishedQueue() {
        return new Queue(QUEUE_ORDER_FINISHED, true);
    }

    // Binding é o contrato que liga a fila ao exchange: mensagem com routing key "order.created" vai para orderCreatedQueue
    @Bean
    public Binding bindingOrderCreated(Queue orderCreatedQueue, DirectExchange pedidosExchange) {
        return BindingBuilder.bind(orderCreatedQueue).to(pedidosExchange).with(ROUTING_KEY_CREATED);
    }

    // Binding é o contrato que liga a fila ao exchange: mensagem com routing key "order.finished" vai para orderFinishedQueue
    @Bean
    public Binding bindingOrderFinished(Queue orderFinishedQueue, DirectExchange pedidosExchange) {
        return BindingBuilder.bind(orderFinishedQueue).to(pedidosExchange).with(ROUTING_KEY_FINISHED);
    }

    // Sem este converter, o Spring enviaria as mensagens como bytes brutos.
    // Com ele, qualquer objeto Java é serializado para JSON automaticamente ao publicar e desserializado ao consumir
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
