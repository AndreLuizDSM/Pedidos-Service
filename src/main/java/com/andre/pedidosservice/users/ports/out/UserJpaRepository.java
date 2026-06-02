package com.andre.pedidosservice.users.ports.out;

import com.andre.pedidosservice.users.entities.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, String> {

    boolean existsByEmail (String email);

    Optional<UserEntity> findByEmail (String email);

    @Transactional
    void deleteById (String id);
}
