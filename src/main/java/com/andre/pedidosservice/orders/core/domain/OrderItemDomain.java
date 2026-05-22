package com.andre.pedidosservice.orders.core.domain;

import com.andre.pedidosservice.products.core.domain.ProductDomain;

//Pegarei o produto , pode ser mais de um , e colocarei no Order.
public class OrderItemDomain {

    private String id;
    private String productId;
    private String productName;
    private double price;
    private Integer quantity;

    //ManyToOne
    private ProductDomain product;
    private OrderDomain order;
}
