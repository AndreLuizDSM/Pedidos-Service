package com.andre.pedidosservice.users.ports.in;

import com.andre.pedidosservice.users.core.domain.UserDomain;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserJpaRepository extends JpaRepository<UserDomain, String> {


    boolean existsByEmail (String email);

    String loginUser(UserDomain domain);

    Optional<UserDomain> findById (String id);

    @Transactional
    void deleteById (String id);
}
