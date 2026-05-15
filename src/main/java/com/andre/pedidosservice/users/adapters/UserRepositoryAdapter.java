package com.andre.pedidosservice.users.adapters;

import com.andre.pedidosservice.users.core.domain.UserDomain;
import com.andre.pedidosservice.users.dtos.UserMapper;
import com.andre.pedidosservice.users.entities.StatusEnum;
import com.andre.pedidosservice.users.entities.UserEntity;
import com.andre.pedidosservice.users.gateways.in.IUserRepository;
import com.andre.pedidosservice.users.ports.in.IUserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.springframework.util.Assert.notNull;

@Service
public class UserRepositoryAdapter implements IUserRepository {

    @Autowired
    private IUserJpaRepository jpaRepository;
    @Autowired
    private UserMapper mapper;


    @Override
    public UserDomain saveUser(UserDomain domain) {

        UserEntity entity = mapper.domainToEntity(domain);
        verifyEmail(entity.getEmail());
        entity.setStatus(StatusEnum.CLIENTE);
        jpaRepository.save(entity);

        return mapper.entityToDomain(entity);
    }

    @Override
    public boolean verifyEmail(String email) {
        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }

        if (jpaRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já existe");
        }

        return true;
    }

    @Override
    public String loginUser(UserDomain domain) {
        notNull(domain, "Campo obrigatório");
        return "";
    }

    @Override
    public Optional<UserDomain> findUserById(String id) {

        return jpaRepository.findById(id).map(mapper::entityToDomain);
    }

    @Override
    public void deleteUser(UserDomain domain) {
        UserEntity entity = mapper.domainToEntity(domain);
        jpaRepository.deleteById(entity.getId());
    }
}
