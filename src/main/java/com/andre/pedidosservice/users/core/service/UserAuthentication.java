package com.andre.pedidosservice.users.core.service;

import com.andre.pedidosservice.users.core.domain.UserDomain;
import com.andre.pedidosservice.users.gateways.in.IUserAuthenticationOut;
import com.andre.pedidosservice.users.gateways.out.IUserAuthenticationIn;

public class UserAuthentication implements IUserAuthenticationIn {

    private final IUserAuthenticationOut authentication;

    public UserAuthentication(IUserAuthenticationOut authentication) {
        this.authentication = authentication;
    }

    @Override
    public String login(UserDomain domain) {

        return authentication.userLogin(domain);
    }
}
