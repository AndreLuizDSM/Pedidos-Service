package com.andre.pedidosservice.users.core.service;

import com.andre.pedidosservice.exception.exceptions.InvalidRequestException;
import com.andre.pedidosservice.exception.exceptions.ResourceNotFoundException;
import com.andre.pedidosservice.users.core.domain.UserDomain;
import com.andre.pedidosservice.users.core.UserStatus;
import com.andre.pedidosservice.users.gateways.out.IUserRepository;
import com.andre.pedidosservice.users.gateways.in.IUserServiceGateway;


public class UserService implements IUserServiceGateway {

    private final IUserRepository repository;

    public UserService(IUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDomain saveUser(String name ,String email, String password) {
        // Criação de usuário para validar o recebimento de dados e persistir depois
        UserDomain domain = UserDomain.newUser(name, email, password);
        if (repository.existsByEmail(domain.getEmail())) {
            throw new InvalidRequestException("Email já existe");
        }

        return repository.saveUser(domain);
    }

    @Override
    public UserDomain getUserById(String id) {
        return repository.findUserById(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuário não encontrado" + id));
    }

    @Override
    public void deleteUser(String id) {
        getUserById(id);
        repository.deleteUser(id);
    }

    @Override
    public UserDomain patchStatus(String id, UserStatus status) {

        return repository.patchUserStatus(id , status);
    }
}
