package com.andre.pedidosservice.notification.adapters.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Nome do Exchange — ponto central que recebe todas as mensagens e as roteia para as filas certas
    public static final String EXCHANGE = "pedidos.exchange";

    // Exchange de dead letter: recebe mensagens rejeitadas (sem requeue) pelas filas principais
    // depois que o retry do listener esgota as tentativas
    public static final String EXCHANGE_DLX = "pedidos.exchange.dlx";

    // Nomes das filas — cada fila armazena mensagens de um evento específico até o consumer consumi-las
    public static final String QUEUE_ORDER_CREATED  = "queue.order.created";
    public static final String QUEUE_ORDER_FINISHED = "queue.order.finished";

    // Filas de dead letter — destino final das mensagens que falharam em todas as tentativas de retry
    public static final String QUEUE_ORDER_CREATED_DLQ  = "queue.order.created.dlq";
    public static final String QUEUE_ORDER_FINISHED_DLQ = "queue.order.finished.dlq";

    // Routing keys — "etiquetas" que o producer coloca na mensagem para o Exchange saber para qual fila enviar
    public static final String ROUTING_KEY_CREATED  = "order.created";
    public static final String ROUTING_KEY_FINISHED = "order.finished";

    // DirectExchange roteia a mensagem para a fila cuja routing key é exatamente igual à da mensagem enviada
    @Bean
    public DirectExchange pedidosExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public DirectExchange pedidosDlx() {
        return new DirectExchange(EXCHANGE_DLX);
    }

    // durable = true garante que a fila não seja perdida se o RabbitMQ reiniciar
    // x-dead-letter-exchange/x-dead-letter-routing-key: quando o retry do listener esgota as
    // tentativas, o container rejeita a mensagem sem requeue (RejectAndDontRequeueRecoverer) — sem
    // esses argumentos ela seria simplesmente descartada; com eles, o RabbitMQ a republica na DLX
    @Bean
    public Queue orderCreatedQueue() {
        return QueueBuilder.durable(QUEUE_ORDER_CREATED)
                .withArgument("x-dead-letter-exchange", EXCHANGE_DLX)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY_CREATED)
                .build();
    }

    @Bean
    public Queue orderFinishedQueue() {
        return QueueBuilder.durable(QUEUE_ORDER_FINISHED)
                .withArgument("x-dead-letter-exchange", EXCHANGE_DLX)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY_FINISHED)
                .build();
    }

    @Bean
    public Queue orderCreatedDlq() {
        return QueueBuilder.durable(QUEUE_ORDER_CREATED_DLQ).build();
    }

    @Bean
    public Queue orderFinishedDlq() {
        return QueueBuilder.durable(QUEUE_ORDER_FINISHED_DLQ).build();
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

    // Liga a fila de dead letter à DLX usando a mesma routing key — assim a mensagem
    // dead-lettered de orderCreatedQueue cai exatamente em orderCreatedDlq
    @Bean
    public Binding bindingOrderCreatedDlq(Queue orderCreatedDlq, DirectExchange pedidosDlx) {
        return BindingBuilder.bind(orderCreatedDlq).to(pedidosDlx).with(ROUTING_KEY_CREATED);
    }

    @Bean
    public Binding bindingOrderFinishedDlq(Queue orderFinishedDlq, DirectExchange pedidosDlx) {
        return BindingBuilder.bind(orderFinishedDlq).to(pedidosDlx).with(ROUTING_KEY_FINISHED);
    }

    // Sem este converter, o Spring enviaria as mensagens como bytes brutos.
    // Com ele, qualquer objeto Java é serializado para JSON automaticamente ao publicar e desserializado ao consumir
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
