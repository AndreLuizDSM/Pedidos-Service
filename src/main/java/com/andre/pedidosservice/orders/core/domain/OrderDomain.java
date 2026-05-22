package com.andre.pedidosservice.orders.core.domain;

import com.andre.pedidosservice.orders.core.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderDomain {

    private String id;
    private LocalDateTime creaded;
    private LocalDateTime updatet;
    private double totalAmount;
    private OrderStatus status;

    //OneToMany
    private List<OrderItemDomain> orderItemDomain = new ArrayList<>();

    public OrderDomain() {
    }

    public OrderDomain(String id, LocalDateTime creaded, LocalDateTime updatet, double totalAmount, OrderStatus status,OrderItemDomain orderItemDomain) {
        this.id = id;
        this.creaded = creaded;
        this.updatet = updatet;
        this.totalAmount = totalAmount;
        this.status = status;

        this.orderItemDomain.add(orderItemDomain);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreaded() {
        return creaded;
    }

    public void setCreaded(LocalDateTime creaded) {
        this.creaded = creaded;
    }

    public LocalDateTime getUpdatet() {
        return updatet;
    }

    public void setUpdatet(LocalDateTime updatet) {
        this.updatet = updatet;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItemDomain> getOrderItemDomain() {
        return orderItemDomain;
    }

    public void setOrderItemDomain(List<OrderItemDomain> orderItemDomain) {
        this.orderItemDomain = orderItemDomain;
    }
}
