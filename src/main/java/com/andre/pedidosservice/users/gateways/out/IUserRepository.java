package com.andre.pedidosservice.users.gateways.out;

import com.andre.pedidosservice.users.core.domain.UserDomain;
import com.andre.pedidosservice.users.core.UserStatus;

import java.util.Optional;

public interface IUserRepository {

    UserDomain saveUser (UserDomain domain);

    Optional<UserDomain> findUserById (String id);

    void deleteUser (String id);

    UserDomain patchUserStatus(String id , UserStatus status);


}
