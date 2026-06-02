package com.andre.pedidosservice.products.core.service;

import com.andre.pedidosservice.exception.exceptions.ResourceNotFoundException;
import com.andre.pedidosservice.products.core.domain.ProductDomain;
import com.andre.pedidosservice.products.gateways.in.IProductServiceGateway;
import com.andre.pedidosservice.products.gateways.out.IProductRepositoryGateway;

import java.util.List;

public class ProductService implements IProductServiceGateway {

    // Porta de saída injetada via construtor — desacopla o serviço do mecanismo de persistência
    private final IProductRepositoryGateway repository;

    public ProductService(IProductRepositoryGateway repository) {
        this.repository = repository;
    }

    @Override
    public ProductDomain createProduct(ProductDomain domain) {
        // O ID é gerado automaticamente na camada de persistência (UUID)
        return repository.saveProduct(domain);
    }

    @Override
    public ProductDomain getProductById(String id) {
        // Lança exceção padronizada se o produto não existir no banco
        return repository.findProductById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
    }

    @Override
    public List<ProductDomain> getAllProducts() {
        return repository.findAllProducts();
    }

    @Override
    public ProductDomain updateProduct(String id, ProductDomain domain) {
        // Busca o produto existente para garantir que ele existe antes de atualizar
        ProductDomain existing = getProductById(id);
        existing.setName(domain.getName());
        existing.setPrice(domain.getPrice());
        existing.setStock(domain.getStock());
        return repository.saveProduct(existing);
    }

    @Override
    public void deleteProduct(String id) {
        // Valida existência antes de deletar para garantir uma resposta 404 explícita
        getProductById(id);
        repository.deleteProduct(id);
    }
}
