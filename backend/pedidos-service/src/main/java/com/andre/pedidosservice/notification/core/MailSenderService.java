package com.andre.pedidosservice.notification.core;

import com.andre.pedidosservice.notification.dtos.OrderNotificationEvent;
import com.andre.pedidosservice.notification.gateways.out.MailSenderServiceGateway;
import com.andre.pedidosservice.user.core.domain.UserDomain;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MailSenderService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final MailSenderServiceGateway service;


    public MailSenderService(JavaMailSender javaMailSender, TemplateEngine templateEngine,
                             MailSenderServiceGateway service){
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.service = service;

    }

    @Value("${envio.email.remetente}")
    private String remetente;

    @Value("${envio.email.nomeRemetente}")
    private String nomeRemetente;

    public void sendOrderCreated(OrderNotificationEvent eventDto) {

            service.sendMailOrderCreated(eventDto);
    }


}