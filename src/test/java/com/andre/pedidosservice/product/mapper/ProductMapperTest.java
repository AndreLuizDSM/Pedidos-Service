package com.andre.pedidosservice.product.mapper;

import com.andre.pedidosservice.product.core.domain.ProductDomain;
import com.andre.pedidosservice.product.dtos.*;
import com.andre.pedidosservice.product.entities.ProductEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class ProductMapperTest {

    ProductMapper mapper;

    ProductEntity productEntity;
    ProductDomain productDomain;
    ProductRequestDTO productRequest;
    ProductResponseDTO productResponseFixture;

    @BeforeEach
    public void setup() {
        mapper = Mappers.getMapper(ProductMapper.class);

        productEntity = new ProductEntity("123", "Teclado", 150.0, 10);
        productDomain = new ProductDomain("123", 150.0, "Teclado", 10);
        productRequest = ProductRequestDTOFixture.build("Teclado", 150.0, 10);
        productResponseFixture = ProductResponseDTOFixture.build("123", "Teclado", 150.0, 10);
    }

    @Test
    void should_ReturnEntity_when_ConvertFromDomain(){
        ProductEntity entity = mapper.domainToEntity(productDomain);

        assertEquals(productEntity, entity);
    }

    @Test
    void should_ReturnResponse_when_ConvertFromDomain(){
        ProductResponseDTO dto = mapper.domainToResponse(productDomain);
        assertEquals(productResponseFixture, dto);
    }

    @Test
    void should_ReturnDomain_when_ConvertFromRequest(){
        ProductDomain domain = mapper.requestToDomain(productRequest);

        // request não traz id, então uso o construtor sem id (id fica null nos dois lados)
        assertEquals(productDomain.getName(), domain.getName());
        assertEquals(productDomain.getStock(), domain.getStock());
        assertEquals(productDomain.getPrice(), domain.getPrice());
        assertNull(domain.getId());;
    }

    @Test
    void should_ReturnDomain_when_ConvertFromEntity(){
        ProductDomain domain = mapper.entityToDomain(productEntity);

        // request não traz id, então uso o construtor sem id (id fica null nos dois lados)
        assertEquals(productDomain.getName(), domain.getName());
        assertEquals(productDomain.getStock(), domain.getStock());
        assertEquals(productDomain.getPrice(), domain.getPrice());
        assertNull(domain.getId());
    }
}
