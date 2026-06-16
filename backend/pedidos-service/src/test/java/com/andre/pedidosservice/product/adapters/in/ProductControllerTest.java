package com.andre.pedidosservice.product.adapters.in;

import com.andre.pedidosservice.product.core.domain.ProductDomain;
import com.andre.pedidosservice.product.dtos.*;
import com.andre.pedidosservice.product.gateways.in.ProductServiceGateway;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @InjectMocks
    ProductController productController;

    @Mock
    ProductServiceGateway service;
    @Mock
    ProductMapper mapper;

    private MockMvc mockMvc;
    private String url;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ProductRequestDTO productRequestDTO;
    private ProductResponseDTO productResponseDTO;
    private ProductDomain productDomain;

    private String json;
    private final String id = "prod-1";

    @BeforeEach
    void setup() throws JsonProcessingException {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).alwaysDo(print()).build();
        url = "/product";

        productRequestDTO = ProductRequestDTOFixture.build("Café", 10.0, 100);
        productResponseDTO = ProductResponseDTOFixture.build(id, "Café", 10.0, 100);
        productDomain = new ProductDomain(id, 10.0, "Café", 100);

        json = objectMapper.writeValueAsString(productRequestDTO);
    }

    @Test
    void should_CreateProduct_when_DataIsValid() throws Exception {
        when(mapper.requestToDomain(productRequestDTO)).thenReturn(productDomain);
        when(service.createProduct(productDomain)).thenReturn(productDomain);
        when(mapper.domainToResponse(productDomain)).thenReturn(productResponseDTO);

        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(status().isOk());

        verify(mapper).requestToDomain(productRequestDTO);
        verify(service).createProduct(productDomain);
        verify(mapper).domainToResponse(productDomain);
        verifyNoMoreInteractions(service);
    }

    @Test
    void should_ReturnBadRequest_when_CreateProductJsonIsNull() throws Exception {
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void should_ReturnBadRequest_when_DataIsInvalid() throws Exception {
        // Nome em branco e preço não positivo violam as validações do ProductRequestDTO
        ProductRequestDTO invalido = ProductRequestDTOFixture.build("", -1.0, -5);
        String invalidoJson = objectMapper.writeValueAsString(invalido);

        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(invalidoJson)
        ).andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void should_ReturnAllProducts_when_ProductsExist() throws Exception {
        when(service.getAllProducts()).thenReturn(List.of(productDomain));
        when(mapper.domainToResponse(productDomain)).thenReturn(productResponseDTO);

        mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        verify(service).getAllProducts();
        verify(mapper).domainToResponse(productDomain);
        verifyNoMoreInteractions(service);
    }

    @Test
    void should_ReturnProduct_when_IdExists() throws Exception {
        when(service.getProductById(id)).thenReturn(productDomain);
        when(mapper.domainToResponse(productDomain)).thenReturn(productResponseDTO);

        mockMvc.perform(get(url + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        verify(service).getProductById(id);
        verify(mapper).domainToResponse(productDomain);
        verifyNoMoreInteractions(service);
    }

    @Test
    void should_UpdateProduct_when_DataIsValid() throws Exception {
        when(mapper.requestToDomain(productRequestDTO)).thenReturn(productDomain);
        when(service.updateProduct(id, productDomain)).thenReturn(productDomain);
        when(mapper.domainToResponse(productDomain)).thenReturn(productResponseDTO);

        mockMvc.perform(put(url + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(status().isOk());

        verify(mapper).requestToDomain(productRequestDTO);
        verify(service).updateProduct(id, productDomain);
        verify(mapper).domainToResponse(productDomain);
        verifyNoMoreInteractions(service);
    }

    @Test
    void should_DeleteProduct_when_IdExists() throws Exception {
        mockMvc.perform(delete(url + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        verify(service).deleteProduct(id);
        verifyNoMoreInteractions(service);
    }
}
