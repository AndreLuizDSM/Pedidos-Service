package com.andre.pedidosservice.users.adapters.config;

import com.andre.pedidosservice.users.core.service.UserService;
import com.andre.pedidosservice.users.gateways.in.IUserRepository;
import com.andre.pedidosservice.users.gateways.out.IUserServiceGateway;
import org.apache.catalina.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdaptersConfig {

    // Bean criado para chamados que não vem por padrão pelo spring, no caso o BCryptPasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoderBCrypt (){
        return new BCryptPasswordEncoder();
    }

    // Bean criado para que ServiceGateway entre nos containers do Spring e possa ser injetado automáticamente
    // no pedido da Controller, sem isso, o Spring ignora o pedido e a controller não tem acesso a Service
    @Bean
    public IUserServiceGateway userRepository(IUserRepository repository){
        return new UserService(repository);
    }
}
