package com.andre.pedidosservice.users.gateways.in;

import com.andre.pedidosservice.users.core.domain.UserDomain;
import com.andre.pedidosservice.users.core.UserStatus;

public interface UserServiceGateway {

    UserDomain createUser(String name, String email, String password);

    UserDomain getUserById(String id);

    void deleteUser(String id);

    UserDomain updateUserStatus(String id, UserStatus status);
}
