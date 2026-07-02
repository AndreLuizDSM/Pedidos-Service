package com.andre.pedidosservice.order.core.domain;

import com.andre.pedidosservice.order.core.OrderStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * Domínio de pedido.
 * Representa um pedido feito por um usuário autenticado, contendo um ou mais itens.
 * O userId vincula o pedido ao usuário dono — aplicado pelo controller via JWT.
 */
public class OrderDomain {

    private String id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiresAt;
    private double totalAmount;
    private OrderStatus status;

    // ID do usuário dono do pedido — extraído do token JWT no controller
    private String userId;

    // OneToMany: um pedido pode ter vários itens
    private List<OrderItemDomain> orderItems = new ArrayList<>();

    public OrderDomain() {
    }

    public OrderDomain(String id, LocalDateTime createdAt, LocalDateTime updatedAt, double totalAmount, OrderStatus status, OrderItemDomain orderItems) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderItems.add(orderItems);
    }

    public static OrderDomain newOrder(){
        OrderDomain domain = new OrderDomain();
        domain.setStatus(OrderStatus.PENDENTE);
        domain.setCreatedAt(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        domain.setUpdatedAt(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        domain.setTotalAmount(0);
        domain.setExpiresAt(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).plusHours(6));
        return domain;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<OrderItemDomain> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDomain> orderItems) {
        this.orderItems = orderItems;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OrderDomain that)) return false;
        return Double.compare(totalAmount, that.totalAmount) == 0 && Objects.equals(id, that.id) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt) && Objects.equals(expiresAt, that.expiresAt) && status == that.status && Objects.equals(userId, that.userId) && Objects.equals(orderItems, that.orderItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, updatedAt, expiresAt, totalAmount, status, userId, orderItems);
    }

    @Override
    public String toString() {
        return "OrderDomain{" +
                "id='" + id + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", expiresAt=" + expiresAt +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                ", userId='" + userId + '\'' +
                ", orderItems=" + orderItems +
                '}';
    }
}
