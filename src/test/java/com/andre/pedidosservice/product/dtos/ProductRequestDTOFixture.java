package com.andre.pedidosservice.product.dtos;

public class ProductRequestDTOFixture {

    public static ProductRequestDTO build(String name,
                                   double price,
                                   Integer stock){
        return new ProductRequestDTO(name, price, stock);
    }
}
