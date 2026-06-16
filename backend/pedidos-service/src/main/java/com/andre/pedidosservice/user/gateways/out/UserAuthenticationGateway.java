package com.andre.pedidosservice.user.gateways.out;

import com.andre.pedidosservice.user.core.domain.UserDomain;

public interface UserAuthenticationGateway {

    String userLogin (UserDomain domain);
}
