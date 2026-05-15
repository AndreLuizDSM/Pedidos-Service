package com.andre.pedidosservice.users.core.service;

import com.andre.pedidosservice.users.core.domain.UserDomain;
import com.andre.pedidosservice.users.entities.StatusEnum;
import com.andre.pedidosservice.users.gateways.in.IUserRepository;
import com.andre.pedidosservice.users.gateways.out.IUserServiceGateway;

public class UserService implements IUserServiceGateway {

    private final IUserRepository repository;

    public UserService(IUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public String login(UserDomain domain) {

        return "";
    }

    @Override
    public UserDomain saveUser(UserDomain domain) {

        return repository.saveUser(domain);
    }

    @Override
    public UserDomain getUserById(String id) {
        return repository.findUserById(id).orElseThrow(
                () -> new IllegalArgumentException("Usuário não encontrado" + id));
    }


    @Override
    public void deleteUser(String id) {

        repository.deleteUser(id);
    }

    @Override
    public UserDomain patchStatus(String id, StatusEnum status) {

        return repository.patchUserStatus(id , status);
    }
}
