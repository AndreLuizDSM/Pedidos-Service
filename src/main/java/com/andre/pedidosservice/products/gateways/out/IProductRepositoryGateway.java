package com.andre.pedidosservice.products.gateways.out;

import com.andre.pedidosservice.products.core.domain.ProductDomain;

import java.util.List;
import java.util.Optional;

/*
 * Porta de saída do módulo Products.
 * Define o contrato que o adapter de persistência deve implementar.
 * O serviço de domínio depende apenas desta interface, nunca do JPA diretamente.
 */
public interface IProductRepositoryGateway {

    ProductDomain saveProduct(ProductDomain domain);

    Optional<ProductDomain> findProductById(String id);

    List<ProductDomain> findAllProducts();

    void deleteProduct(String id);
}
