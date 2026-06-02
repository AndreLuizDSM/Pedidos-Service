package com.andre.pedidosservice.orders.entities;

import com.andre.pedidosservice.products.entities.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

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
    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
