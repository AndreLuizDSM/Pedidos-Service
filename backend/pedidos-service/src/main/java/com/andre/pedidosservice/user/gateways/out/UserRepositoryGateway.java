package com.andre.pedidosservice.user.gateways.out;

import com.andre.pedidosservice.user.core.domain.UserDomain;
import com.andre.pedidosservice.user.core.UserStatus;

import java.util.Optional;

public interface UserRepositoryGateway {

    UserDomain save(UserDomain domain);

    Optional<UserDomain> findById(String id);

    boolean existsByEmail(String email);

    void deleteById(String id);

    UserDomain updateStatus(String id, UserStatus status);


}
