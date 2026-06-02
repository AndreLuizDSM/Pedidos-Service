package com.andre.pedidosservice.orders.dtos;

// DTO de saída para um item do pedido — inclui subtotal calculado (price * quantity)
public record OrderItemResponseDTO(
        String id,
        String productId,
        String productName,
        double price,
        Integer quantity,
        double subtotal
) {}
