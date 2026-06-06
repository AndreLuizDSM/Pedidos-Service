package com.andre.pedidosservice.order.dtos;

public class OrderItemRequestDTOFixture {

    public OrderItemRequestDTO build(String productId,
                                     Integer quantity){
        return new OrderItemRequestDTO(productId, quantity);
    }
}
