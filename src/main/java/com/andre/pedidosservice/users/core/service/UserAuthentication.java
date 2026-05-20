package com.andre.pedidosservice.users.core.service;

import com.andre.pedidosservice.users.core.domain.UserDomain;
import com.andre.pedidosservice.users.gateways.in.IUserAuthenticationIn;
import com.andre.pedidosservice.users.gateways.out.IUserAuthenticationOut;

public class UserAuthentication implements IUserAuthenticationOut {

    private final IUserAuthenticationIn authentication;

    public UserAuthentication(IUserAuthenticationIn authentication) {
        this.authentication = authentication;
    }

    @Override
    public String login(UserDomain domain) {

        return authentication.userLogin(domain);
    }
}
