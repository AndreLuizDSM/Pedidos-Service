package com.andre.pedidosservice.product.adapters.out;

import com.andre.pedidosservice.product.core.domain.ProductDomain;
import com.andre.pedidosservice.product.dtos.ProductMapper;
import com.andre.pedidosservice.product.entities.ProductEntity;
import com.andre.pedidosservice.product.ports.out.ProductJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryAdapterTest {

    @InjectMocks
    private ProductRepositoryAdapter adapter;

    @Mock
    private ProductJpaRepository jpaRepository;
    @Mock
    private ProductMapper mapper;

    private ProductDomain productDomain;
    private ProductEntity productEntity;

    @BeforeEach
    void setup() {
        // O adapter só faz o mapeamento domain<->entity e delega ao JPA, sem regra de negócio
        productDomain = new ProductDomain("prod-1", 10.0, "Café", 100);

        productEntity = new ProductEntity();
        productEntity.setId("prod-1");
        productEntity.setName("Café");
        productEntity.setPrice(10.0);
        productEntity.setStock(100);
    }

                        // save

    @Test
    void should_MapAndSave_when_SaveIsCalled() {
        when(mapper.domainToEntity(productDomain)).thenReturn(productEntity);
        when(jpaRepository.save(productEntity)).thenReturn(productEntity);
        when(mapper.entityToDomain(productEntity)).thenReturn(productDomain);

        ProductDomain result = adapter.save(productDomain);

        assertNotNull(result);
        assertEquals(productDomain, result);

        verify(mapper).domainToEntity(productDomain);
        verify(jpaRepository).save(productEntity);
        verify(mapper).entityToDomain(productEntity);
        verifyNoMoreInteractions(jpaRepository);
    }

                        // findById

    @Test
    void should_ReturnProduct_when_IdExists() {
        when(jpaRepository.findById("prod-1")).thenReturn(Optional.of(productEntity));
        when(mapper.entityToDomain(productEntity)).thenReturn(productDomain);

        Optional<ProductDomain> result = adapter.findById("prod-1");

        assertTrue(result.isPresent());
        assertEquals(productDomain, result.get());

        verify(jpaRepository).findById("prod-1");
        verify(mapper).entityToDomain(productEntity);
        verifyNoMoreInteractions(jpaRepository);
    }

    @Test
    void should_ReturnEmpty_when_IdDoesNotExist() {
        when(jpaRepository.findById("prod-1")).thenReturn(Optional.empty());

        Optional<ProductDomain> result = adapter.findById("prod-1");

        assertFalse(result.isPresent());

        verify(jpaRepository).findById("prod-1");
        verifyNoMoreInteractions(jpaRepository);
    }

                        // findAll

    @Test
    void should_ReturnProducts_when_FindAllIsCalled() {
        when(jpaRepository.findAll()).thenReturn(List.of(productEntity));
        when(mapper.entityToDomain(productEntity)).thenReturn(productDomain);

        List<ProductDomain> result = adapter.findAll();

        assertEquals(1, result.size());
        assertEquals(productDomain, result.get(0));

        verify(jpaRepository).findAll();
        verify(mapper).entityToDomain(productEntity);
        verifyNoMoreInteractions(jpaRepository);
    }

                        // deleteById

    @Test
    void should_DelegateToRepository_when_DeleteByIdIsCalled() {
        adapter.deleteById("prod-1");

        verify(jpaRepository).deleteById("prod-1");
        verifyNoMoreInteractions(jpaRepository);
    }
}
