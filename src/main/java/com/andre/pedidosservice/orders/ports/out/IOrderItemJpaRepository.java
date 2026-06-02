package com.andre.pedidosservice.orders.ports.out;

import com.andre.pedidosservice.orders.entities.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositório JPA dedicado aos itens de pedido — permite buscar um item diretamente pelo seu ID
@Repository
public interface IOrderItemJpaRepository extends JpaRepository<OrderItemEntity, String> {
}
