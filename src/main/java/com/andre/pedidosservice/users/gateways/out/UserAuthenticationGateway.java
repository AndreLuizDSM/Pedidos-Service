package com.andre.pedidosservice.users.gateways.out;

import com.andre.pedidosservice.users.core.domain.UserDomain;

public interface UserAuthenticationGateway {

    String userLogin (UserDomain domain);
}
