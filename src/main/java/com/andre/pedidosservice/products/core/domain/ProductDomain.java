package com.andre.pedidosservice.products.core.domain;

public class ProductDomain {

    private String id;
    private double price;
    private String name;
    private Integer stock;

    public ProductDomain() {
    }

    public ProductDomain(String id, double price, String name, Integer stock) {
        this.id = id;
        this.price = price;
        this.name = name;
        this.stock = stock;
    }

    public ProductDomain(double price, String name, Integer stock) {
        this.price = price;
        this.name = name;
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
