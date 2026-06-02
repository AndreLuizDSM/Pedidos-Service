package com.andre.pedidosservice.users.adapters.out;

import com.andre.pedidosservice.exception.exceptions.UnauthorizedException;
import com.andre.pedidosservice.security.JwtUtil;
import com.andre.pedidosservice.users.core.domain.UserDomain;
import com.andre.pedidosservice.users.entities.UserEntity;
import com.andre.pedidosservice.users.gateways.out.IUserAuthenticationOut;
import com.andre.pedidosservice.users.ports.out.IUserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import static org.springframework.util.Assert.notNull;


// Adapter separado para realizar apenas a authenticação do usuário

@RequiredArgsConstructor
@Service
public class UserAuthenticationAdapter implements IUserAuthenticationOut {

    private final IUserJpaRepository jpaRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public String userLogin(UserDomain domain) {

        try {
            UserEntity entity = jpaRepository.findByEmail(domain.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));


            //AuthenticationManager compara os valores de domain com o do banco de dados , e usa loadByUserName
            //authentication é o return de userDetails do loadByUserName
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(entity.getId(), domain.getPassword())
            );

            return jwtUtil.generateToken(authentication.getName(), entity.getStatus());

        } catch (BadCredentialsException | UsernameNotFoundException e) {
            throw new UnauthorizedException("Credenciais invalidas");
        }

    }
}
