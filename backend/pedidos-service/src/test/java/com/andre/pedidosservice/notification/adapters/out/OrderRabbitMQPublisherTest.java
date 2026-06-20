package com.andre.pedidosservice.notification.adapters.out;

import com.andre.pedidosservice.notification.adapters.config.RabbitMQConfig;
import com.andre.pedidosservice.notification.dtos.OrderNotificationEvent;
import com.andre.pedidosservice.order.core.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderRabbitMQPublisherTest {

    @InjectMocks
    private OrderRabbitMQPublisher rabbitMQPublisher;

    @Mock
    private RabbitTemplate rabbitTemplate;

    private OrderNotificationEvent eventDto;

    @BeforeEach
    public void setup() {

        eventDto = OrderNotificationEvent.builder()
                .orderId("123Order")
                .userId("123User")
                .status(OrderStatus.CONFIRMADO)
                .totalAmount(50)
                .occurredAt(LocalDateTime.now())
                .build();
    }

    // TODO REVER ESSE TESTE DEPOIS
    @Test
    public void should_notifyCreateOrder(){

        doNothing().when(rabbitTemplate).convertAndSend(RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_CREATED, eventDto);

        rabbitMQPublisher.notifyOrderCreated(eventDto);

        verify(rabbitTemplate).convertAndSend(RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_CREATED, eventDto);
        verifyNoMoreInteractions(rabbitTemplate);


    }

    @Test
    public void should_notifyFinishedOrder(){

        doNothing().when(rabbitTemplate).convertAndSend(RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_FINISHED, eventDto);

        rabbitMQPublisher.notifyOrderFinished(eventDto);

        verify(rabbitTemplate).convertAndSend(RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_FINISHED, eventDto);
        verifyNoMoreInteractions(rabbitTemplate);


    }
}
