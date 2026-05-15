package com.andre.pedidosservice.users.gateways.out;

import com.andre.pedidosservice.users.core.domain.UserDomain;

public interface IUserServiceGateway {

    String login (UserDomain domain);

    UserDomain saveUser (UserDomain domain);

    UserDomain getUserById (String id);

    void deleteUser (UserDomain domain);
}
