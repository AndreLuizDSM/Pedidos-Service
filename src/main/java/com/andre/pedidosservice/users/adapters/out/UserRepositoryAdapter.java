package com.andre.pedidosservice.users.adapters.out;

import com.andre.pedidosservice.exception.exceptions.InvalidRequestException;
import com.andre.pedidosservice.exception.exceptions.ResourceNotFoundException;
import com.andre.pedidosservice.exception.exceptions.UnauthorizedException;
import com.andre.pedidosservice.security.JwtUtil;
import com.andre.pedidosservice.users.core.domain.UserDomain;
import com.andre.pedidosservice.users.dtos.UserMapper;
import com.andre.pedidosservice.users.core.UserStatus;
import com.andre.pedidosservice.users.entities.UserEntity;
import com.andre.pedidosservice.users.gateways.in.IUserRepository;
import com.andre.pedidosservice.users.ports.in.IUserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.springframework.util.Assert.notNull;

@RequiredArgsConstructor
@Service
public class UserRepositoryAdapter implements IUserRepository {


    private final IUserJpaRepository jpaRepository;
    private final UserMapper mapper;

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;


    @Override
    public UserDomain saveUser(UserDomain domain) {

        UserEntity entity = mapper.domainToEntity(domain);
        verifyEmail(entity.getEmail());
        entity.setStatus(UserStatus.CLIENT);

        String encoded = passwordEncoder.encode(domain.getPassword());
        entity.setPassword(encoded);
        jpaRepository.save(entity);

        return mapper.entityToDomain(entity);
    }

    public boolean verifyEmail(String email) {
        if (!email.contains("@") || !email.contains(".")) {
            throw new InvalidRequestException("Credenciais inválidas");
        }

        if (jpaRepository.existsByEmail(email)) {
            throw new InvalidRequestException("Email já existe");
        }

        return true;
    }

    @Override
    public String loginUser(UserDomain domain) {

        try {
            notNull(domain, "Campo obrigatório");
            UserEntity entity = jpaRepository.findByEmail(domain.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));


            //AuthenticationManager compara os valores de domain com o do banco de dados , e usa loadByUserName
            //authentication é o return de userDetails do loadByUserName
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(entity.getId(), domain.getPassword())
            );

            // getAuthorities() retorna uma Collection ; iterator percorre uma Collection ;
            // next acessa o primeiro item da Collection ; getAuthority() retorna como String;
            String getStatus = authentication.getAuthorities().iterator().next().getAuthority();

            return jwtUtil.generateToken(authentication.getName(), UserStatus.valueOf(getStatus));

        } catch (BadCredentialsException | UsernameNotFoundException e) {
            throw new UnauthorizedException(e.getMessage());
        }

    }

    @Override
    public Optional<UserDomain> findUserById(String id) {

        return jpaRepository.findById(id).map(mapper::entityToDomain);
    }

    @Override
    public void deleteUser(String id) {

        jpaRepository.deleteById(id);
    }

    @Override
    public UserDomain patchUserStatus(String id, UserStatus status) {
        UserEntity entity = jpaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuário não existe")
        );
        entity.setStatus(status);

        return mapper.entityToDomain(jpaRepository.save(entity));
    }


}
