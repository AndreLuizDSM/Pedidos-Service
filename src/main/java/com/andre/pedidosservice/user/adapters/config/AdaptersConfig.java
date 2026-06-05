package com.andre.pedidosservice.user.adapters.config;

import com.andre.pedidosservice.user.core.service.UserAuthentication;
import com.andre.pedidosservice.user.core.service.UserService;
import com.andre.pedidosservice.user.gateways.out.UserAuthenticationGateway;
import com.andre.pedidosservice.user.gateways.out.UserRepositoryGateway;
import com.andre.pedidosservice.user.gateways.in.UserAuthenticationService;
import com.andre.pedidosservice.user.gateways.in.UserServiceGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdaptersConfig {

    // Bean criado para chamados que não vem por padrão pelo spring, no caso o BCryptPasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder (){
        return new BCryptPasswordEncoder();
    }

    // Bean criado para que ServiceGateway entre nos containers do Spring e possa ser injetado automáticamente
    // no pedido da Controller, sem isso, o Spring ignora o pedido e a controller não tem acesso a Service
    @Bean
    public UserServiceGateway userRepository(UserRepositoryGateway repository){
        return new UserService(repository);
    }

    @Bean
    public UserAuthenticationService userAuthentication(UserAuthenticationGateway authenticationIn){
        return new UserAuthentication(authenticationIn);
    }
}
