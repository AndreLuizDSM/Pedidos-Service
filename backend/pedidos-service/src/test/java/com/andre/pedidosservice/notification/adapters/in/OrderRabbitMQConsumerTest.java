package com.andre.pedidosservice.notification.adapters.in;

import com.andre.pedidosservice.notification.dtos.OrderNotificationEvent;
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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderRabbitMQConsumerTest {

    @InjectMocks
    private OrderRabbitMQConsumer rabbitMQConsumer;

    @Mock
    private Message message;

    private OrderNotificationEvent eventDto;

    @BeforeEach
    public void setup() {

       // Criando mensagem com prioridade para teste
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setPriority(2);

        when(message.getMessageProperties()).thenReturn(messageProperties);

        eventDto = OrderNotificationEvent.builder()
                .orderId("123Order")
                .userId("123User")
                .status(OrderStatus.CONFIRMADO)
                .totalAmount(50)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void should_ConsumeCreatedMessage(){

        rabbitMQConsumer.consumeCreatedMessage(message, eventDto);

        verify(message, times(1)).getMessageProperties();
    }

    @Test
    public void should_ConsumeFinishedMessage(){

        rabbitMQConsumer.consumeFinishedMessage(message, eventDto);

        verify(message, times(1)).getMessageProperties();
    }
}
