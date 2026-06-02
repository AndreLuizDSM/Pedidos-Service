package com.andre.pedidosservice.products.adapters.config;

import com.andre.pedidosservice.products.core.service.ProductService;
import com.andre.pedidosservice.products.gateways.in.IProductServiceGateway;
import com.andre.pedidosservice.products.gateways.out.IProductRepositoryGateway;
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
    public IProductServiceGateway productService(IProductRepositoryGateway repository) {
        return new ProductService(repository);
    }
}
