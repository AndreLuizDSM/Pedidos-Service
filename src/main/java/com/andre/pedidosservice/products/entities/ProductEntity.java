package com.andre.pedidosservice.products.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Entidade JPA que representa a tabela de produtos no banco de dados
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "table_product")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name", length = 120, nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private double price;

    // Estoque: controla quantas unidades restam disponíveis para venda
    @Column(name = "stock", nullable = false)
    private Integer stock;
}
