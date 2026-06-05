package com.andre.pedidosservice.user.gateways.in;

import com.andre.pedidosservice.user.core.domain.UserDomain;

public interface UserAuthenticationService {

    String login (UserDomain domain);
}
