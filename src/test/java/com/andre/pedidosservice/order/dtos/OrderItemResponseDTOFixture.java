package com.andre.pedidosservice.order.dtos;

public class OrderItemResponseDTOFixture {

    public OrderItemResponseDTO build(String id,
                                      String productId,
                                      String productName,
                                      double price,
                                      Integer quantity,
                                      double subtotal){
        return new OrderItemResponseDTO(id, productId, productName, price, quantity, subtotal);
    }
}
