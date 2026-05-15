package com.andre.pedidosservice.users.core.service;

import com.andre.pedidosservice.orders.core.domain.OrderDomain;
import com.andre.pedidosservice.users.core.domain.UserDomain;
import com.andre.pedidosservice.users.gateways.in.IUserRepository;
import com.andre.pedidosservice.users.gateways.out.IUserServiceGateway;

import java.util.Optional;

import static org.springframework.util.Assert.notNull;

public class UserService implements IUserServiceGateway {

    private final IUserRepository repository;
    private final UserDomain userDomain;

    public UserService(IUserRepository repository, UserDomain userDomain) {
        this.repository = repository;
        this.userDomain = userDomain;
    }

    @Override
    public String login(UserDomain domain) {
        if (!repository.verifyEmail(domain.getEmail())) {
            throw new IllegalArgumentException("Conta não existe");
        }
            String tokenJWT = repository.loginUser(domain);

        return tokenJWT;
    }

    @Override
    public UserDomain saveUser(UserDomain domain) {

        if (repository.verifyEmail(domain.getEmail())) {
            throw new IllegalArgumentException("Email já existe");
        }

        return repository.saveUser(domain);
    }

    @Override
    public UserDomain getUserById(String id) {
        return repository.findUserById(id).orElseThrow(
                () -> new IllegalArgumentException("Usuário não encontrado" + id));
    }

    @Override
    public void deleteUser(UserDomain domain) {

        repository.deleteUser(domain);
    }
}
