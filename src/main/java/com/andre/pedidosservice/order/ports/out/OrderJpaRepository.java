package com.andre.pedidosservice.order.ports.out;

import com.andre.pedidosservice.order.core.OrderStatus;
import com.andre.pedidosservice.order.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

// Porta de infraestrutura de pedidos: Spring Data JPA fornece save, findById, deleteById automaticamente
@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, String> {

    List<OrderEntity> findByUserId(String userId);

    List<OrderEntity> findByStatusAndExpiresAtBefore(OrderStatus status, LocalDateTime dateTime);

    void deleteByStatus(OrderStatus status);
}
