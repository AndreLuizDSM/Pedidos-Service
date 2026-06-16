package com.andre.pedidosservice.order.core.domain;

import java.util.Objects;

/*
 * Domínio de um item dentro de um pedido.
 * Representa um produto específico adicionado ao pedido, com snapshot de preço e nome
 * no momento da compra (garante histórico mesmo que o produto seja editado depois).
 */
public class OrderItemDomain {

    private String id;

    // ID do produto referenciado — usado para buscar o produto no adapter
    private String productId;

    // Snapshot do nome do produto no momento da compra
    private String productName;

    // Snapshot do preço do produto no momento da compra
    private double productPrice;

    private Integer quantity;

    public OrderItemDomain() {
    }

    public OrderItemDomain(String productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OrderItemDomain that)) return false;
        return Double.compare(productPrice, that.productPrice) == 0 && Objects.equals(id, that.id) && Objects.equals(productId, that.productId) && Objects.equals(productName, that.productName) && Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productId, productName, productPrice, quantity);
    }

    @Override
    public String toString() {
        return "OrderItemDomain{" +
                "id='" + id + '\'' +
                ", productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", productPrice=" + productPrice +
                ", quantity=" + quantity +
                '}';
    }
}
