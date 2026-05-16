package com.andre.pedidosservice.users.gateways.out;

import com.andre.pedidosservice.users.core.domain.UserDomain;
import com.andre.pedidosservice.users.core.UserStatus;

public interface IUserServiceGateway {

    String login (UserDomain domain);

    UserDomain saveUser (UserDomain domain);

    UserDomain getUserById (String id);

    void deleteUser (String id);

    UserDomain patchStatus (String id, UserStatus status);
}
