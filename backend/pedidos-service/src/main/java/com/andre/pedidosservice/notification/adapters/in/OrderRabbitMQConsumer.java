package com.andre.pedidosservice.notification.adapters.in;

import com.andre.pedidosservice.notification.adapters.config.RabbitMQConfig;
import com.andre.pedidosservice.notification.core.MailSenderService;
import com.andre.pedidosservice.notification.dtos.OrderNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderRabbitMQConsumer {

    private final MailSenderService mailSenderService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(RabbitMQConfig.QUEUE_ORDER_CREATED),
    exchange = @Exchange(RabbitMQConfig.EXCHANGE),
    key = RabbitMQConfig.ROUTING_KEY_CREATED))
    public void consumeCreatedMessage(final Message message, final OrderNotificationEvent event) {

        log.info("Consume Created");
        log.info("Prioridade {}" , message.getMessageProperties().getPriority());
        log.info("Evento: {}", event);

        // Captura aqui em vez de deixar propagar: sem isso, uma falha no envio do e-mail
        // (SMTP fora do ar, usuário não encontrado, etc.) faria o Spring AMQP rejeitar a
        // mensagem e o RabbitMQ reentregá-la indefinidamente — travando a fila por causa de
        // um efeito colateral (notificação) que não é crítico para o processamento do pedido
        try {
            mailSenderService.sendOrderCreated(event);
        } catch (RuntimeException e) {
            log.error("Falha ao enviar e-mail de confirmação para o pedido {}", event.getOrderId(), e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(RabbitMQConfig.QUEUE_ORDER_FINISHED),
            exchange = @Exchange(RabbitMQConfig.EXCHANGE),
            key = RabbitMQConfig.ROUTING_KEY_FINISHED))
    public void consumeFinishedMessage(final Message message, final OrderNotificationEvent event) {

        log.info("Consume Finished");
        log.info("Prioridade {}" , message.getMessageProperties().getPriority());
        log.info("Evento: {}", event);
    }
}
