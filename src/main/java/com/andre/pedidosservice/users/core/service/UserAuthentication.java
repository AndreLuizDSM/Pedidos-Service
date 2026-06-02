package com.andre.pedidosservice.users.core.service;

import com.andre.pedidosservice.users.core.domain.UserDomain;
import com.andre.pedidosservice.users.gateways.out.UserAuthenticationGateway;
import com.andre.pedidosservice.users.gateways.in.UserAuthenticationService;

public class UserAuthentication implements UserAuthenticationService {

    private final UserAuthenticationGateway authentication;

    public UserAuthentication(UserAuthenticationGateway authentication) {
        this.authentication = authentication;
    }

    @Override
    public String login(UserDomain domain) {

        return authentication.userLogin(domain);
    }
}
