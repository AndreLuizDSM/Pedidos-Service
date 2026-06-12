package com.andre.pedidosservice.security;

import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtRequestFilterTest {

    @InjectMocks
    private JwtRequestFilter filter;

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;

    // Limpa o contexto de segurança entre os testes para não vazar autenticação de um teste para outro
    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void should_NotAuthenticateAndContinueChain_when_NoAuthorizationHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        // Verifica que não foi passado token
        assertThat(SecurityContextHolder.getContext().getAuthentication(), is(nullValue()));
        // Verifica se o chain fez o .doFilter
        verify(chain).doFilter(request, response);
    }

    @Test
    void should_AuthenticateAndContinueChain_when_TokenIsValid() throws Exception {
        UserDetails userDetails = User.withUsername("user-1")
                .password("senha123").authorities("CLIENT").build();

        // Cria um token para ser injetado no SecurityContextHolder
        when(request.getHeader("Authorization")).thenReturn("Bearer token-valido");
        // Aplica todos os métodos para validar o TOKEN
        when(jwtUtil.extrairSubjectUUIDToken("token-valido")).thenReturn("user-1");
        when(userDetailsService.loadUserByUsername("user-1")).thenReturn(userDetails);
        when(jwtUtil.validateToken("token-valido", "user-1")).thenReturn(true);

        // Chama o método do filtro
        filter.doFilterInternal(request, response, chain);

        // Verifica que o token não é NULL e o seu valor
        assertThat(SecurityContextHolder.getContext().getAuthentication(), notNullValue());
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName(), is("user-1"));
        // Verifica o filtro
        verify(chain).doFilter(request, response);

    }

    @Test
    void should_Return401AndNotContinueChain_when_TokenIsInvalid() throws Exception {
        // Cria um token inválido
        when(request.getHeader("Authorization")).thenReturn("Bearer token-invalido");
        // Throw erro do token inválido
        when(jwtUtil.extrairSubjectUUIDToken("token-invalido"))
                .thenThrow(new MalformedJwtException("token ruim"));
        when(request.getRequestURI()).thenReturn("/orders");
        // getWriter precisa devolver um PrintWriter real para o filtro escrever o JSON de erro
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        filter.doFilterInternal(request, response, chain);
        // Verifica que não ouve token
        assertThat(SecurityContextHolder.getContext().getAuthentication(), is(nullValue()));
        // Verifica o status response
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(chain, never()).doFilter(request, response);
    }
}
