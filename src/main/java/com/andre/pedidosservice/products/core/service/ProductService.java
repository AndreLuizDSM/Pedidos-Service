package com.andre.pedidosservice.products.core.service;

import com.andre.pedidosservice.exception.exceptions.ResourceNotFoundException;
import com.andre.pedidosservice.products.core.domain.ProductDomain;
import com.andre.pedidosservice.products.gateways.in.ProductServiceGateway;
import com.andre.pedidosservice.products.gateways.out.ProductRepositoryGateway;

import java.util.List;

public class ProductService implements ProductServiceGateway {

    // Porta de saída injetada via construtor — desacopla o serviço do mecanismo de persistência
    private final ProductRepositoryGateway repository;

    public ProductService(ProductRepositoryGateway repository) {
        this.repository = repository;
    }

    @Override
    public ProductDomain createProduct(ProductDomain domain) {
        return repository.save(domain);
    }

    @Override
    public ProductDomain getProductById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
    }

    @Override
    public List<ProductDomain> getAllProducts() {
        return repository.findAll();
    }

    @Override
    public ProductDomain updateProduct(String id, ProductDomain domain) {
        ProductDomain existing = getProductById(id);
        existing.setName(domain.getName());
        existing.setPrice(domain.getPrice());
        existing.setStock(domain.getStock());
        return repository.save(existing);
    }

    @Override
    public void deleteProduct(String id) {
        getProductById(id);
        repository.deleteById(id);
    }
}
