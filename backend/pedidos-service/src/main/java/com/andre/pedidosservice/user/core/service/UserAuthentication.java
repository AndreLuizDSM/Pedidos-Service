package com.andre.pedidosservice.user.core.service;

import com.andre.pedidosservice.user.core.domain.UserDomain;
import com.andre.pedidosservice.user.gateways.out.UserAuthenticationGateway;
import com.andre.pedidosservice.user.gateways.in.UserAuthenticationService;

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
