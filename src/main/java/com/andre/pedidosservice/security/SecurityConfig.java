package com.andre.pedidosservice.security;

import com.andre.pedidosservice.user.core.UserStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


// Configura quais rotas precisam de autenticação , e faz o filtro chain
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Instâncias de JwtUtil e UserDetailsService injetadas pelo Spring
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;


    // Construtor para injeção de dependências de JwtUtil e UserDetailsService
    public SecurityConfig(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    // Configuração do filtro de segurança
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Cria uma instância do JwtRequestFilter com JwtUtil e UserDetailsService
        JwtRequestFilter jwtRequestFilter = new JwtRequestFilter(jwtUtil, userDetailsService);

        http
                .csrf(AbstractHttpConfigurer::disable) // Desativa proteção CSRF para APIs REST (não aplicável a
                // APIs que não mantêm estado)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/login").permitAll() // Permite acesso ao
                        // endpoint de login sem autenticação
                        .requestMatchers(HttpMethod.POST, "/user").permitAll() // Permite acesso ao endpoint
                        // POST usuario sem autenticação
                        .requestMatchers(HttpMethod.DELETE, "/user/**").hasAuthority(UserStatus.ADMIN.name())
                        .requestMatchers("/user/**").authenticated() // Requer autenticação para qualquer endpoint
                        // que comece com /user/

                        // Somente ADMIN pode criar, atualizar e deletar produtos do catálogo
                        .requestMatchers(HttpMethod.POST, "/product").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/product/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/product/**").hasAuthority("ADMIN")

                        .anyRequest().authenticated() // Requer autenticação para todas as outras requisições
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Configura a política de
                        // sessão como stateless (sem sessão)
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class); // Adiciona o
        // filtro JWT antes do filtro de autenticação padrão

        // Retorna a configuração do filtro de segurança construída
        return http.build();
    }

    // Configura o AuthenticationManager usando AuthenticationConfiguration
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        // Obtém e retorna o AuthenticationManager da configuração de autenticação
        return authenticationConfiguration.getAuthenticationManager();
    }
}