package com.andre.pedidosservice.orders.adapters.config;

import com.andre.pedidosservice.orders.core.service.OrderService;
import com.andre.pedidosservice.orders.gateways.in.OrderExpirationGatewayService;
import com.andre.pedidosservice.orders.gateways.in.OrderGatewayService;
import com.andre.pedidosservice.orders.gateways.out.OrderRepositoryGateway;
import com.andre.pedidosservice.products.gateways.out.ProductRepositoryGateway;
import com.andre.pedidosservice.users.gateways.out.UserRepositoryGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderAdaptersConfig {

    @Bean
    public OrderGatewayService orderService(OrderRepositoryGateway repository,
                                            ProductRepositoryGateway productRepository,
                                            UserRepositoryGateway userRepository) {
        return new OrderService(repository, productRepository, userRepository);
    }

    @Bean
    public OrderExpirationGatewayService orderExpirationService(OrderRepositoryGateway repository) {
        return new com.andre.pedidosservice.orders.core.service.OrderExpirationService(repository);
    }
}
