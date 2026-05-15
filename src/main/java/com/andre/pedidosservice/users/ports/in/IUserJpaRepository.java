package com.andre.pedidosservice.users.ports.in;

import com.andre.pedidosservice.users.core.domain.UserDomain;
import com.andre.pedidosservice.users.entities.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserJpaRepository extends JpaRepository<UserEntity, String> {


    boolean existsByEmail (String email);

    String loginUser(UserDomain domain);

    Optional<UserEntity> findById (String id);

    @Transactional
    void deleteById (String id);
}
