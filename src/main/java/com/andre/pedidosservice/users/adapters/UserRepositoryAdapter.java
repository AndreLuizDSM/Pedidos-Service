package com.andre.pedidosservice.users.adapters;

import com.andre.pedidosservice.users.core.domain.UserDomain;
import com.andre.pedidosservice.users.gateways.in.IUserRepository;
import com.andre.pedidosservice.users.ports.in.IUserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserRepositoryAdapter implements IUserRepository {

    @Autowired
    private IUserJpaRepository jpaRepository;

    @Override
    public UserDomain saveUser(UserDomain domain) {
        return null;
    }

    @Override
    public Boolean verifyEmail(String email) {
        return null;
    }

    @Override
    public String loginUser(UserDomain domain) {
        return "";
    }

    @Override
    public Optional<UserDomain> findUserById(String id) {
        return Optional.empty();
    }

    @Override
    public void deleteUser(UserDomain domain) {

    }
}
