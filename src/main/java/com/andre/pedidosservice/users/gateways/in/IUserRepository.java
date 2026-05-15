package com.andre.pedidosservice.users.gateways.in;

import com.andre.pedidosservice.users.core.domain.UserDomain;

import java.util.Optional;

public interface IUserRepository {

    UserDomain saveUser (UserDomain domain);

    boolean verifyEmail (String email);

    String loginUser(UserDomain domain);

    Optional<UserDomain> findUserById (String id);

    void deleteUser (UserDomain domain);
}
