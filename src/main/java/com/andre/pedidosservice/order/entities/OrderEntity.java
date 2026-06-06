package com.andre.pedidosservice.order.entities;

import com.andre.pedidosservice.order.core.OrderStatus;
import com.andre.pedidosservice.user.entities.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * Entidade JPA que representa a tabela de pedidos no banco de dados.
 * ManyToOne com UserEntity: um usuário pode ter vários pedidos.
 * OneToMany com OrderItemEntity: um pedido pode ter vários itens.
 * cascade + orphanRemoval: itens são criados/deletados automaticamente junto com o pedido.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "table_order")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "total_amount")
    private double totalAmount;

    @Column(name = "status")
    private OrderStatus status;

    // Equivale ao userId no Domain
    // ManyToOne: vários pedidos pertencem a um usuário — FK user_id na tabela de pedidos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // OneToMany: um pedido tem N itens; orphanRemoval remove itens do banco quando removidos da lista
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> orderItems = new ArrayList<>();


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OrderEntity that)) return false;
        return Double.compare(totalAmount, that.totalAmount) == 0 && Objects.equals(id, that.id) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt) && Objects.equals(expiresAt, that.expiresAt) && status == that.status && Objects.equals(user, that.user) && Objects.equals(orderItems, that.orderItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, updatedAt, expiresAt, totalAmount, status, user, orderItems);
    }

    @Override
    public String toString() {
        return "OrderEntity{" +
                "id='" + id + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", expiresAt=" + expiresAt +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                ", user=" + user +
                ", orderItems=" + orderItems +
                '}';
    }
}
