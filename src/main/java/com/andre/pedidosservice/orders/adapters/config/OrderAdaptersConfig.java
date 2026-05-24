package com.andre.pedidosservice.orders.adapters.config;

import com.andre.pedidosservice.orders.core.service.OrderService;
import com.andre.pedidosservice.orders.gateways.in.IOrderGatewayService;
import com.andre.pedidosservice.orders.gateways.out.IOrderRepositoryGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderAdaptersConfig {

    @Bean
    public IOrderGatewayService orderService(IOrderRepositoryGateway repository) {
        return new OrderService(repository);
    }
}
