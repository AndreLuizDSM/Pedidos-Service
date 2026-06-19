package com.andre.pedidosservice.notification.adapters.in;

import com.andre.pedidosservice.notification.core.MailSenderService;
import com.andre.pedidosservice.notification.dtos.OrderNotificationEvent;
import com.andre.pedidosservice.notification.gateways.out.NotificationDeduplicationGateway;
import com.andre.pedidosservice.order.core.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderRabbitMQConsumerTest {

    @InjectMocks
    private OrderRabbitMQConsumer rabbitMQConsumer;

    @Mock
    private Message message;

    @Mock
    private MailSenderService mailSenderService;

    @Mock
    private NotificationDeduplicationGateway deduplicationGateway;

    private OrderNotificationEvent eventDto;

    @BeforeEach
    public void setup() {

       // Criando mensagem com prioridade para teste
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setPriority(2);

        when(message.getMessageProperties()).thenReturn(messageProperties);

        eventDto = OrderNotificationEvent.builder()
                .eventId("evt-1")
                .orderId("123Order")
                .userId("123User")
                .status(OrderStatus.CONFIRMADO)
                .totalAmount(50)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void should_ConsumeCreatedMessage(){

        when(deduplicationGateway.alreadyProcessed("evt-1")).thenReturn(false);

        rabbitMQConsumer.consumeCreatedMessage(message, eventDto);

        verify(message, times(1)).getMessageProperties();
        verify(mailSenderService, times(1)).sendOrderCreated(eventDto);
        verify(deduplicationGateway, times(1)).markAsProcessed("evt-1");
    }

    @Test
    public void should_SkipCreatedMessage_when_AlreadyProcessed(){

        when(deduplicationGateway.alreadyProcessed("evt-1")).thenReturn(true);

        rabbitMQConsumer.consumeCreatedMessage(message, eventDto);

        verify(mailSenderService, never()).sendOrderCreated(any());
        verify(deduplicationGateway, never()).markAsProcessed(any());
    }

    @Test
    public void should_PropagateException_when_MailSenderFails(){

        when(deduplicationGateway.alreadyProcessed("evt-1")).thenReturn(false);
        doThrow(new RuntimeException("Falha no SMTP")).when(mailSenderService).sendOrderCreated(eventDto);

        // Deve propagar: é o retry com backoff do container RabbitMQ que trata essa falha,
        // não o próprio listener
        assertThrows(RuntimeException.class,
                () -> rabbitMQConsumer.consumeCreatedMessage(message, eventDto));

        verify(deduplicationGateway, never()).markAsProcessed(any());
    }

    @Test
    public void should_ConsumeFinishedMessage(){

        when(deduplicationGateway.alreadyProcessed("evt-1")).thenReturn(false);

        rabbitMQConsumer.consumeFinishedMessage(message, eventDto);

        verify(message, times(1)).getMessageProperties();
        verify(mailSenderService, times(1)).sendOrderFinished(eventDto);
        verify(deduplicationGateway, times(1)).markAsProcessed("evt-1");
    }

    @Test
    public void should_SkipFinishedMessage_when_AlreadyProcessed(){

        when(deduplicationGateway.alreadyProcessed("evt-1")).thenReturn(true);

        rabbitMQConsumer.consumeFinishedMessage(message, eventDto);

        verify(mailSenderService, never()).sendOrderFinished(any());
        verify(deduplicationGateway, never()).markAsProcessed(any());
    }

    @Test
    public void should_PropagateException_when_MailSenderFails_OnFinished(){

        when(deduplicationGateway.alreadyProcessed("evt-1")).thenReturn(false);
        doThrow(new RuntimeException("Falha no SMTP")).when(mailSenderService).sendOrderFinished(eventDto);

        assertThrows(RuntimeException.class,
                () -> rabbitMQConsumer.consumeFinishedMessage(message, eventDto));

        verify(deduplicationGateway, never()).markAsProcessed(any());
    }
}
