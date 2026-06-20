package com.andre.pedidosservice.notification.core;

import com.andre.pedidosservice.notification.dtos.OrderNotificationEvent;
import com.andre.pedidosservice.notification.gateways.out.MailSenderServiceGateway;

public class MailSenderService {

    private final MailSenderServiceGateway service;

    public MailSenderService(MailSenderServiceGateway service){
        this.service = service;
    }

    public void sendOrderCreated(OrderNotificationEvent eventDto) {
            service.sendMailOrderCreated(eventDto);
    }

    public void sendOrderFinished(OrderNotificationEvent eventDto) {
            service.sendMailOrderFinished(eventDto);
    }


}