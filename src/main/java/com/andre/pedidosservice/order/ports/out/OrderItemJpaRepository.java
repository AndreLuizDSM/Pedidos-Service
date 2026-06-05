package com.andre.pedidosservice.order.ports.out;

import com.andre.pedidosservice.order.entities.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositório JPA dedicado aos itens de pedido — permite buscar um item diretamente pelo seu ID
@Repository
public interface OrderItemJpaRepository extends JpaRepository<OrderItemEntity, String> {
}
