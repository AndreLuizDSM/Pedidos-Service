package com.andre.pedidosservice.users.gateways.in;

import com.andre.pedidosservice.users.core.domain.UserDomain;

public interface UserAuthenticationService {

    String login (UserDomain domain);
}
