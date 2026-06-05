package com.andre.pedidosservice.product.adapters.out;

import com.andre.pedidosservice.product.core.domain.ProductDomain;
import com.andre.pedidosservice.product.dtos.ProductMapper;
import com.andre.pedidosservice.product.entities.ProductEntity;
import com.andre.pedidosservice.product.gateways.out.ProductRepositoryGateway;
import com.andre.pedidosservice.product.ports.out.ProductJpaRepository;
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
public class ProductRepositoryAdapter implements ProductRepositoryGateway {

    private final ProductJpaRepository jpaRepository;
    private final ProductMapper mapper;

    @Override
    public ProductDomain save(ProductDomain domain) {
        ProductEntity entity = mapper.domainToEntity(domain);
        return mapper.entityToDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<ProductDomain> findById(String id) {
        return jpaRepository.findById(id).map(mapper::entityToDomain);
    }

    @Override
    public List<ProductDomain> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }
}
