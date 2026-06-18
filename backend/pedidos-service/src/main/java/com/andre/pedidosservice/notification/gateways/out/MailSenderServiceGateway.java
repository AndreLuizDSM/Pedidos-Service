package com.andre.pedidosservice.notification.gateways.out;

import com.andre.pedidosservice.notification.dtos.OrderNotificationEvent;
import org.springframework.mail.javamail.MimeMessageHelper;

public interface MailSenderServiceGateway {

    void sendMailOrderCreated(OrderNotificationEvent event) throws Exception;
}
