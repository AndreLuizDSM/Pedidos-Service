package com.andre.pedidosservice.products.dtos;

// DTO de saída: dados do produto retornados nas respostas da API
public record ProductResponseDTO(
        String id,
        String name,
        double price,
        Integer stock
) {}
