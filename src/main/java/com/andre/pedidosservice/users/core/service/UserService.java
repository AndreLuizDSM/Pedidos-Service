package com.andre.pedidosservice.users.core.service;

import com.andre.pedidosservice.exception.exceptions.InvalidRequestException;
import com.andre.pedidosservice.exception.exceptions.ResourceNotFoundException;
import com.andre.pedidosservice.users.core.domain.UserDomain;
import com.andre.pedidosservice.users.core.UserStatus;
import com.andre.pedidosservice.users.gateways.out.UserRepositoryGateway;
import com.andre.pedidosservice.users.gateways.in.UserServiceGateway;


public class UserService implements UserServiceGateway {

    private final UserRepositoryGateway repository;

    public UserService(UserRepositoryGateway repository) {
        this.repository = repository;
    }

    @Override
    public UserDomain createUser(String name, String email, String password) {
        UserDomain domain = UserDomain.newUser(name, email, password);
        if (repository.existsByEmail(domain.getEmail())) {
            throw new InvalidRequestException("Email já existe");
        }

        return repository.save(domain);
    }

    @Override
    public UserDomain getUserById(String id) {
        return repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuário não encontrado" + id));
    }

    @Override
    public void deleteUser(String id) {
        getUserById(id);
        repository.deleteById(id);
    }

    @Override
    public UserDomain updateUserStatus(String id, UserStatus status) {
        return repository.updateStatus(id, status);
    }
}
