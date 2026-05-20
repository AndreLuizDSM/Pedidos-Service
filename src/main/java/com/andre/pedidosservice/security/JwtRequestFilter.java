package com.andre.pedidosservice.security;

import com.andre.pedidosservice.exception.exceptions.ErrorResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SignatureException;
import java.time.LocalDateTime;

// Intercepta cada request feito lendo o token passado pelo HEADER e faz a autenticação
public class JwtRequestFilter extends OncePerRequestFilter {

    // Define propriedades para armazenar instâncias de JwtUtil e UserDetailsService
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;


    public JwtRequestFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    // Método chamado para fazer o filtro interno na requisição chamada, filtrar o HEADER (JWT) recebido.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

      try {
        // Obtém o valor do header "Authorization" da requisição
        final String authorizationHeader = request.getHeader("Authorization");

        // Verifica se o HEADER existe e começa com "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

            // Extrai o token JWT do cabeçalho
            final String token = authorizationHeader.substring(7);

            // Extrai o SUBJECT do token JWT
            final String userUUID = jwtUtil.extrairSubjectUUIDToken(token);

            // Se o nome de usuário não for nulo e o usuário não estiver autenticado ainda
            if (userUUID != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Carrega os detalhes do usuário a partir do seu UUID ID
                UserDetails userDetails = userDetailsService.loadUserByUsername(userUUID);

                // Valida o token JWT
                if (jwtUtil.validateToken(token, userUUID)) {

                    // Cria um objeto de autenticação com as informações do usuário
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    // Define a autenticação no contexto de segurança
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        // Continua a cadeia de filtros, permitindo que a requisição prossiga
        chain.doFilter(request, response);
    }  catch(ExpiredJwtException | MalformedJwtException e) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(buildError("Token inválido",
                HttpStatus.UNAUTHORIZED.value(),
                request.getRequestURI(),
                e.getMessage()));
        }
    }

    private String buildError (String message, int status, String path, String erro) throws JsonProcessingException {
        // Criar um erroDTO para passar detalhadamente o erro com o Token JWT
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .timeStamp(LocalDateTime.now())
                .status(status)
                .message(message)
                .path(path)
                .erro(erro)
                .build();

        /*
            Transformar objeto em String, HttpResponse.write só escreve Strings;
            Em java antigo, writeValueAsString precisa de um try/catch
            Em java antigo, ObjectMapper não consegue transformar LocalDateTime em String, precisa ser manualmente
         */
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // Serializar o LocalDateTime.now , transformar de um objeto {"year": 2026, "month": 5} para uma string.

        return objectMapper.writeValueAsString(errorResponseDTO);

    }
}