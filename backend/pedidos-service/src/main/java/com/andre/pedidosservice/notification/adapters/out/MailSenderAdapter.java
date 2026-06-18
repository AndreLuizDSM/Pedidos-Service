package com.andre.pedidosservice.notification.adapters.out;

import com.andre.pedidosservice.exception.exceptions.ResourceNotFoundException;
import com.andre.pedidosservice.notification.dtos.OrderNotificationEvent;
import com.andre.pedidosservice.notification.gateways.out.MailSenderServiceGateway;
import com.andre.pedidosservice.user.adapters.out.UserRepositoryGatewayAdapter;
import com.andre.pedidosservice.user.core.domain.UserDomain;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MailSenderAdapter implements MailSenderServiceGateway {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final UserRepositoryGatewayAdapter userRepository;

    @Value("${envio.email.remetente}")
    private String remetente;

    @Value("${envio.email.nomeRemetente}")
    private String nomeRemetente;

    @Override
    public void sendMailOrderCreated(OrderNotificationEvent event) throws Exception {
        try {

            UserDomain user = userRepository.findById(event.getUserId()).orElseThrow(
                    ()-> new ResourceNotFoundException("Usuario não encontrado " + event.getUserId()));

            MimeMessage mensagem = javaMailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mensagem, true,
                    StandardCharsets.UTF_8.name());
            mimeMessageHelper.setFrom(new InternetAddress(remetente, nomeRemetente));
            mimeMessageHelper.setTo(InternetAddress.parse(user.getEmail()));
            mimeMessageHelper.setSubject("Notificação de Pedido");


            Context context = new Context();
            context.setVariable("totalAmount", event.getTotalAmount());
            context.setVariable("dateNow", LocalDateTime.now());
            String template = templateEngine.process("ordercreated", context);
            mimeMessageHelper.setText(template, true);
            javaMailSender.send(mensagem);

        } catch ( UnsupportedEncodingException | jakarta.mail.MessagingException e){
            throw new Exception("Erro ao enviar email ", e);
        }
    }
}
