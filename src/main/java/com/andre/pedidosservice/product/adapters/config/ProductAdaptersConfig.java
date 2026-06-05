package com.andre.pedidosservice.product.adapters.config;

import com.andre.pedidosservice.product.core.service.ProductService;
import com.andre.pedidosservice.product.gateways.in.ProductServiceGateway;
import com.andre.pedidosservice.product.gateways.out.ProductRepositoryGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Configuração dos beans do módulo Products.
 * Registra o ProductService como implementação de IProductServiceGateway.
 * O Spring injeta automaticamente o IProductRepositoryGateway (implementado pelo ProductRepositoryAdapter).
 */
@Configuration
public class ProductAdaptersConfig {

    @Bean
    public ProductServiceGateway productService(ProductRepositoryGateway repository) {
        return new ProductService(repository);
    }
}
