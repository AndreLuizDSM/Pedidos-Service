package com.andre.pedidosservice.notification.adapters.in;

import com.andre.pedidosservice.notification.adapters.config.RabbitMQConfig;
import com.andre.pedidosservice.notification.core.MailSenderService;
import com.andre.pedidosservice.notification.dtos.OrderNotificationEvent;
import com.andre.pedidosservice.notification.gateways.out.NotificationDeduplicationGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderRabbitMQConsumer {

    private final MailSenderService mailSenderService;
    private final NotificationDeduplicationGateway deduplicationGateway;

    // A fila, a exchange e o binding já são declarados como beans em RabbitMQConfig (com os
    // argumentos de dead-letter). Declará-los de novo aqui via @QueueBinding criaria uma segunda
    // definição da mesma fila sem esses argumentos, conflitando com a declaração do RabbitMQConfig.
    @RabbitListener(queues = RabbitMQConfig.QUEUE_ORDER_CREATED)
    public void consumeCreatedMessage(final Message message, final OrderNotificationEvent event) {

        log.info("Consume Created");
        log.info("Prioridade {}" , message.getMessageProperties().getPriority());
        log.info("Evento: {}", event);

        // Idempotência: o RabbitMQ garante "at-least-once" — a mesma mensagem pode ser
        // reentregue (ex: consumer caiu antes do ack). Sem essa checagem, o usuário
        // receberia o mesmo e-mail mais de uma vez.
        if (deduplicationGateway.alreadyProcessed(event.getEventId())) {
            log.warn("Evento {} já processado, ignorando duplicata", event.getEventId());
            return;
        }

        // Não captura a exceção aqui de propósito: o container do RabbitMQ está configurado
        // (spring.rabbitmq.listener.simple.retry.*) para tentar de novo com backoff antes de
        // desistir definitivamente da mensagem
        mailSenderService.sendOrderCreated(event);
        deduplicationGateway.markAsProcessed(event.getEventId());
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ORDER_FINISHED)
    public void consumeFinishedMessage(final Message message, final OrderNotificationEvent event) {

        log.info("Consume Finished");
        log.info("Prioridade {}" , message.getMessageProperties().getPriority());
        log.info("Evento: {}", event);

        if (deduplicationGateway.alreadyProcessed(event.getEventId())) {
            log.warn("Evento {} já processado, ignorando duplicata", event.getEventId());
            return;
        }

        mailSenderService.sendOrderFinished(event);
        deduplicationGateway.markAsProcessed(event.getEventId());
    }
}
