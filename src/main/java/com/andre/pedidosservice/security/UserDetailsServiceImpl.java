package com.andre.pedidosservice.security;

import com.andre.pedidosservice.user.entities.UserEntity;
import com.andre.pedidosservice.user.ports.out.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


//Acessar os dados do usuário para pegar seus detalhes para fazer o request das requisições
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // Repositório para acessar dados de usuário no banco de dados
    @Autowired
    private UserJpaRepository jpaRepository;

    // Implementação do método para carregar detalhes do usuário pelo UUID ID
    @Override
    public UserDetails loadUserByUsername(String UUID) throws UsernameNotFoundException {
        // Busca o usuário no banco de dados pelo e-mail
        UserEntity usuario = jpaRepository.findById(UUID)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + UUID));

        // Cria e retorna um objeto UserDetails com base no usuário encontrado
        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getId()) // Define o nome de usuário como o seu UUID ID
                .password(usuario.getPassword())// Define a senha do usuário
                .authorities(usuario.getStatus().name())// Define as suas autoridades
                .build(); // Constrói o objeto UserDetails
    }
}
