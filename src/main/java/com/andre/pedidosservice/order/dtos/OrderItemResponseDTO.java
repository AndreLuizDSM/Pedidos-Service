package com.andre.pedidosservice.order.dtos;

// DTO de saída para um item do pedido — inclui subtotal calculado (price * quantity)
public record OrderItemResponseDTO(
        String id,
        String productId,
        String productName,
        double productPrice,
        Integer quantity,
        double subtotal
) {}
