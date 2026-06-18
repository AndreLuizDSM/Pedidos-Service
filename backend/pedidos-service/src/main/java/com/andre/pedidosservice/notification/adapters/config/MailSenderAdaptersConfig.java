package com.andre.pedidosservice.notification.adapters.config;

import com.andre.pedidosservice.notification.core.MailSenderService;
import com.andre.pedidosservice.notification.gateways.out.MailSenderServiceGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

@Configuration
public class MailSenderAdaptersConfig {

    // JavaMailSender e TemplateEngine já vêm auto-configurados pelo Spring Boot
    // (spring-boot-starter-mail / spring-boot-starter-thymeleaf). O MailSenderServiceGateway
    // é resolvido automaticamente como o MailSenderAdapter, único @Service que implementa essa porta
    @Bean
    public MailSenderService mailSenderService(JavaMailSender javaMailSender, TemplateEngine templateEngine,
                                                MailSenderServiceGateway gateway) {
        return new MailSenderService(javaMailSender, templateEngine, gateway);
    }
}
