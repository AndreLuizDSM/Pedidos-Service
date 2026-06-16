package com.andre.pedidosservice.product.dtos;

public class ProductResponseDTOFixture {

    public static ProductResponseDTO build(String id,
                                    String name,
                                    double price,
                                    Integer stock){
        return new ProductResponseDTO(id, name, price, stock);
    }
}
