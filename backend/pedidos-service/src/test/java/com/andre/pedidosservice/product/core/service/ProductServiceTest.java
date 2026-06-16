package com.andre.pedidosservice.product.core.service;

import com.andre.pedidosservice.exception.exceptions.ResourceNotFoundException;
import com.andre.pedidosservice.product.core.domain.ProductDomain;
import com.andre.pedidosservice.product.gateways.out.ProductRepositoryGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepositoryGateway repository;

    private ProductDomain productDomain;

    @BeforeEach
    void setup() {
        productDomain = new ProductDomain("p1", 10.0, "Café", 100);
    }

                        // createProduct

    @Test
    void should_CreateProduct_when_DataIsValid() {
        when(repository.save(productDomain)).thenReturn(productDomain);

        ProductDomain result = productService.createProduct(productDomain);

        assertNotNull(result);
        assertEquals("Café", result.getName());

        verify(repository).save(productDomain);
        verifyNoMoreInteractions(repository);
    }

                        // getProductById

    @Test
    void should_ReturnProduct_when_IdExists() {
        when(repository.findById("p1")).thenReturn(Optional.of(productDomain));

        ProductDomain result = productService.getProductById("p1");

        assertNotNull(result);
        assertEquals("p1", result.getId());

        verify(repository).findById("p1");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void should_ThrowResourceNotFoundException_when_ProductNotFound() {
        when(repository.findById("p1")).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductById("p1"));

        assertThat(e, notNullValue());
        assertThat(e.getMessage(), is("Produto não encontrado"));

        verify(repository).findById("p1");
        verifyNoMoreInteractions(repository);
    }

                        // getAllProducts

    @Test
    void should_ReturnAllProducts_when_ProductsExist() {
        when(repository.findAll()).thenReturn(List.of(productDomain, new ProductDomain("p2", 5.0, "Pão", 50)));

        List<ProductDomain> result = productService.getAllProducts();

        assertEquals(2, result.size());

        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

                        // updateProduct

    @Test
    void should_UpdateProduct_when_DataIsValid() {
        ProductDomain novosDados = new ProductDomain("p1", 20.0, "Café Premium", 80);
        when(repository.findById("p1")).thenReturn(Optional.of(productDomain));
        when(repository.save(productDomain)).thenReturn(productDomain);

        ProductDomain result = productService.updateProduct("p1", novosDados);

        assertNotNull(result);
        // o serviço copia os novos dados para o domínio existente antes de salvar
        assertEquals("Café Premium", productDomain.getName());
        assertEquals(20.0, productDomain.getPrice());
        assertEquals(80, productDomain.getStock());

        verify(repository).findById("p1");
        verify(repository).save(productDomain);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void should_ThrowResourceNotFoundException_when_ProductNotFoundOnUpdate() {
        when(repository.findById("p1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.updateProduct("p1", productDomain));

        verify(repository).findById("p1");
        verifyNoMoreInteractions(repository);
    }

                        // deleteProduct

    @Test
    void should_DeleteProduct_when_IdExists() {
        when(repository.findById("p1")).thenReturn(Optional.of(productDomain));

        productService.deleteProduct("p1");

        verify(repository).findById("p1");
        verify(repository).deleteById("p1");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void should_ThrowResourceNotFoundException_when_ProductNotFoundOnDelete() {
        when(repository.findById("p1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct("p1"));

        verify(repository).findById("p1");
        verifyNoMoreInteractions(repository);
    }
}
