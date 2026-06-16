package com.andre.pedidosservice.product.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

// DTO de entrada: dados necessários para criar ou atualizar um produto
public record ProductRequestDTO(

        @NotBlank(message = "Nome do produto é obrigatório")
        String name,

        @Positive(message = "Preço deve ser maior que zero")
        double price,

        @Min(value = 0, message = "Estoque não pode ser negativo")
        Integer stock
) {}
