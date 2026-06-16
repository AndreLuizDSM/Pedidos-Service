package com.andre.pedidosservice.order.adapters.config;

import com.andre.pedidosservice.order.core.service.OrderService;
import com.andre.pedidosservice.order.gateways.in.OrderExpirationGatewayService;
import com.andre.pedidosservice.order.gateways.in.OrderGatewayService;
import com.andre.pedidosservice.order.gateways.out.OrderRepositoryGateway;
import com.andre.pedidosservice.product.gateways.out.ProductRepositoryGateway;
import com.andre.pedidosservice.user.gateways.out.UserRepositoryGateway;
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
        return new com.andre.pedidosservice.order.core.service.OrderExpirationService(repository);
    }
}
