package com.andre.pedidosservice.product.core.domain;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProductDomain that)) return false;
        return Double.compare(price, that.price) == 0 && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(stock, that.stock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, price, name, stock);
    }

    @Override
    public String toString() {
        return "ProductDomain{" +
                "id='" + id + '\'' +
                ", price=" + price +
                ", name='" + name + '\'' +
                ", stock=" + stock +
                '}';
    }
}
