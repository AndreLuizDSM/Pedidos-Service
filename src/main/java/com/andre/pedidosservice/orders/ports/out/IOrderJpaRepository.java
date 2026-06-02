package com.andre.pedidosservice.orders.ports.out;

import com.andre.pedidosservice.orders.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Porta de infraestrutura de pedidos: Spring Data JPA fornece save, findById, deleteById automaticamente
@Repository
public interface IOrderJpaRepository extends JpaRepository<OrderEntity, String> {

    // Busca todos os pedidos de um usuário específico — usado em getOrdersByUserId
    List<OrderEntity> findByUserId(String userId);
}
