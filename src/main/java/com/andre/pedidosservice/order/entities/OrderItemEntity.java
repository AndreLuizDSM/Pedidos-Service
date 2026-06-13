package com.andre.pedidosservice.order.entities;

import com.andre.pedidosservice.product.entities.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

/*
 * Entidade JPA que representa um item dentro de um pedido.
 * ManyToOne com OrderEntity: um item pertence a um único pedido.
 * ManyToOne com ProductEntity: o item referencia qual produto foi adicionado.
 *
 * productName e price são snapshots: copiam o valor do produto no momento da compra.
 * Isso garante que o histórico do pedido não mude se o produto for editado depois.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "table_order_item")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // ManyToOne: vários itens pertencem a um pedido — FK order_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    // ManyToOne: o item referencia o produto — FK product_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    // Snapshot do nome do produto no momento da compra
    @Column(name = "product_name", length = 120, nullable = false)
    private String productName;

    // Snapshot do preço do produto no momento da compra
    @Column(name = "product_price", nullable = false)
    private double productPrice;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OrderItemEntity that)) return false;
        return Double.compare(productPrice, that.productPrice) == 0 && Objects.equals(id, that.id) && Objects.equals(order, that.order) && Objects.equals(product, that.product) && Objects.equals(productName, that.productName) && Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, order, product, productName, productPrice, quantity);
    }

    @Override
    public String toString() {
        return "OrderItemEntity{" +
                "id='" + id + '\'' +
                ", order=" + order +
                ", product=" + product +
                ", productName='" + productName + '\'' +
                ", productPrice=" + productPrice +
                ", quantity=" + quantity +
                '}';
    }
}
