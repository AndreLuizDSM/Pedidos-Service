package com.andre.pedidosservice.users.gateways.out;

import com.andre.pedidosservice.users.core.domain.UserDomain;
import com.andre.pedidosservice.users.core.UserStatus;

import java.util.Optional;

public interface UserRepositoryGateway {

    UserDomain save(UserDomain domain);

    Optional<UserDomain> findById(String id);

    boolean existsByEmail(String email);

    void deleteById(String id);

    UserDomain updateStatus(String id, UserStatus status);


}
