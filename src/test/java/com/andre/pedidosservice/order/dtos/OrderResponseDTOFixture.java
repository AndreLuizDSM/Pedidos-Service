package com.andre.pedidosservice.order.dtos;

import com.andre.pedidosservice.order.core.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDTOFixture {

    public OrderResponseDTO build(String id,
                                  LocalDateTime createdAt,
                                  LocalDateTime updatedAt,
                                  LocalDateTime expiresAt,
                                  double totalAmount,
                                  OrderStatus status,
                                  String userId,
                                  List<OrderItemResponseDTO> items){
        return new OrderResponseDTO(id, createdAt, updatedAt, expiresAt,
                totalAmount, status, userId, items);
    }
}
