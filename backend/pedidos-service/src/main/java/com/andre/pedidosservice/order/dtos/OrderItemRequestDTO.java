package com.andre.pedidosservice.order.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// DTO de entrada para adicionar um item ao pedido: informa qual produto e quanto
public record OrderItemRequestDTO(

        @NotBlank(message = "ID do produto é obrigatório")
        String productId,

        @Min(value = 1, message = "Quantidade mínima é 1")
        Integer quantity
) {}
