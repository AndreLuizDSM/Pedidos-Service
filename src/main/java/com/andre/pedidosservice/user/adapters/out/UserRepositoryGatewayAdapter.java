package com.andre.pedidosservice.user.adapters.out;

import com.andre.pedidosservice.exception.exceptions.ResourceNotFoundException;
import com.andre.pedidosservice.user.core.UserStatus;
import com.andre.pedidosservice.user.core.domain.UserDomain;
import com.andre.pedidosservice.user.dtos.UserMapper;
import com.andre.pedidosservice.user.entities.UserEntity;
import com.andre.pedidosservice.user.gateways.out.UserRepositoryGateway;
import com.andre.pedidosservice.user.ports.out.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserRepositoryGatewayAdapter implements UserRepositoryGateway {


    private final UserJpaRepository jpaRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDomain save(UserDomain domain) {
        UserEntity entity = mapper.domainToEntity(domain);
        String encoded = passwordEncoder.encode(domain.getPassword());
        entity.setPassword(encoded);
        jpaRepository.save(entity);
        return mapper.entityToDomain(entity);
    }

    @Override
    public Optional<UserDomain> findById(String id) {
        return jpaRepository.findById(id).map(mapper::entityToDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public void deleteById(String id) {
        if (findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Usuário não encontrado " + id);
        }
        jpaRepository.deleteById(id);
    }

    @Override
    public UserDomain updateStatus(String id, UserStatus status) {
        UserEntity entity = jpaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuário não encontrado " + id)
        );
        entity.setStatus(status);
        return mapper.entityToDomain(jpaRepository.save(entity));
    }


}
