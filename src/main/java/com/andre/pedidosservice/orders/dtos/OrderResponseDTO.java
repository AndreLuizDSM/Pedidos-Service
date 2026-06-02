package com.andre.pedidosservice.orders.dtos;

import com.andre.pedidosservice.orders.core.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

// DTO de saída para um pedido completo — retornado em todas as respostas do OrderController
public record OrderResponseDTO(
        String id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime expiresAt,
        double totalAmount,
        OrderStatus status,
        String userId,
        List<OrderItemResponseDTO> items
) {}
