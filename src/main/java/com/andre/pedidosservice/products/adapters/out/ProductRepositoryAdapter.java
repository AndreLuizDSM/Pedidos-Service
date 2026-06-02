package com.andre.pedidosservice.products.adapters.out;

import com.andre.pedidosservice.products.core.domain.ProductDomain;
import com.andre.pedidosservice.products.dtos.ProductMapper;
import com.andre.pedidosservice.products.entities.ProductEntity;
import com.andre.pedidosservice.products.gateways.out.IProductRepositoryGateway;
import com.andre.pedidosservice.products.ports.out.IProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*
 * Adapter de saída: implementa a porta IProductRepositoryGateway.
 * É a única classe que conhece o JPA — isola a infraestrutura do domínio.
 * Faz a tradução entre ProductDomain (domínio) e ProductEntity (banco de dados).
 */
@Service
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements IProductRepositoryGateway {

    private final IProductJpaRepository jpaRepository;
    private final ProductMapper mapper;

    @Override
    public ProductDomain saveProduct(ProductDomain domain) {
        // Converte domain para entity, persiste e retorna o domain com ID gerado
        ProductEntity entity = mapper.domainToEntity(domain);
        return mapper.entityToDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<ProductDomain> findProductById(String id) {
        return jpaRepository.findById(id).map(mapper::entityToDomain);
    }

    @Override
    public List<ProductDomain> findAllProducts() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProduct(String id) {
        jpaRepository.deleteById(id);
    }
}
