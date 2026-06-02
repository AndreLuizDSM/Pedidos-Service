package com.andre.pedidosservice.products.gateways.in;

import com.andre.pedidosservice.products.core.domain.ProductDomain;

import java.util.List;

/*
 * Porta de entrada do módulo Products.
 * É o contrato que o controller usa para se comunicar com a camada de serviço.
 * Isola o controller de qualquer implementação concreta do serviço.
 */
public interface ProductServiceGateway {

    ProductDomain createProduct(ProductDomain domain);

    ProductDomain getProductById(String id);

    List<ProductDomain> getAllProducts();

    // Atualiza nome, preço e estoque de um produto existente
    ProductDomain updateProduct(String id, ProductDomain domain);

    void deleteProduct(String id);
}
