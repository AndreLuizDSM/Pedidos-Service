package com.andre.pedidosservice.product.ports.out;

import com.andre.pedidosservice.product.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Porta de infraestrutura: Spring Data JPA fornece automaticamente save, findById, findAll, deleteById
@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, String> {
}
