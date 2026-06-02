package com.andre.pedidosservice.products.dtos;

import com.andre.pedidosservice.products.core.domain.ProductDomain;
import com.andre.pedidosservice.products.entities.ProductEntity;
import org.mapstruct.Mapper;

/*
 * Mapper MapStruct: gera automaticamente as implementações de conversão em tempo de compilação.
 * componentModel = "spring" permite que o Spring injete o mapper como um bean normal.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    // Converte entidade JPA para domain (usado na saída do adapter de persistência)
    ProductDomain entityToDomain(ProductEntity entity);

    // Converte domain para entidade JPA (usado na entrada do adapter de persistência)
    ProductEntity domainToEntity(ProductDomain domain);

    // Converte DTO de request para domain (usado no controller ao receber dados da API)
    ProductDomain requestToDomain(ProductRequestDTO dto);

    // Converte domain para DTO de resposta (usado no controller ao retornar dados)
    ProductResponseDTO domainToResponse(ProductDomain domain);
}
