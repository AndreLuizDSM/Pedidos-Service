package com.andre.pedidosservice.users.gateways.in;

import com.andre.pedidosservice.users.core.domain.UserDomain;

public interface IUserAuthenticationIn {

    String login (UserDomain domain);
}
