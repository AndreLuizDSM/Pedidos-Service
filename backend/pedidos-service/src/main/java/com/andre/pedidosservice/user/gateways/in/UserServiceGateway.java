package com.andre.pedidosservice.user.gateways.in;

import com.andre.pedidosservice.user.core.domain.UserDomain;
import com.andre.pedidosservice.user.core.UserStatus;

public interface UserServiceGateway {

    UserDomain createUser(String name, String email, String password);

    UserDomain getUserById(String id);

    void deleteUser(String id);

    UserDomain updateUserStatus(String id, UserStatus status);
}
