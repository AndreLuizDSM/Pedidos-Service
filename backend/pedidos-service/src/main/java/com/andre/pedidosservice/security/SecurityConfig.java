package com.andre.pedidosservice.security;

import com.andre.pedidosservice.exception.exceptions.ErrorResponseDTO;
import com.andre.pedidosservice.user.core.UserStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.LocalDateTime;


// Configura quais rotas precisam de autenticação , e faz o filtro chain
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Instâncias de JwtUtil e UserDetailsService injetadas pelo Spring
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    private final String authorities = "ADMIN";


    // Construtor para injeção de dependências de JwtUtil e UserDetailsService
    public SecurityConfig(JwtUtil jwtUtil, UserDetailsService userDetailsService, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
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
                        .requestMatchers(HttpMethod.POST, "/product").hasAuthority(authorities)
                        .requestMatchers(HttpMethod.PUT, "/product/**").hasAuthority(authorities)
                        .requestMatchers(HttpMethod.DELETE, "/product/**").hasAuthority(authorities)

                        .anyRequest().authenticated() // Requer autenticação para todas as outras requisições
                )
                // Handling próprio do HttpSecurity
                .exceptionHandling(ex -> ex
                        // Sem credencial ou token inválido -> 401 Unauthorized
                        .authenticationEntryPoint((request, response, authException) ->
                                buildError(request, response, HttpStatus.UNAUTHORIZED, "Unauthorized"))
                        // Autenticado, mas sem permissão (ex: não-ADMIN tentando DELETE) -> 403 Forbidden
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                buildError(request, response, HttpStatus.FORBIDDEN, "Forbidden"))
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

    // Escreve o mesmo ErrorResponseDTO do GlobalExceptionHandler para erros do filtro de segurança
    // (401/403), que não passam pelo @ControllerAdvice por ocorrerem antes do controller.
    private void buildError(HttpServletRequest request, HttpServletResponse response,
                            HttpStatus status, String erro) throws IOException {
        ErrorResponseDTO body = ErrorResponseDTO.builder()
                .timeStamp(LocalDateTime.now())
                .status(status.value())
                .message(erro)
                .path(request.getRequestURI())
                .erro(erro)
                .build();

        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), body);
    }
}