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
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void semHeaderAuthorization_naoAutentica_eSegueACadeia() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication(), is(nullValue()));
        verify(chain).doFilter(request, response);
    }

    @Test
    void tokenValido_autentica_eSegueACadeia() throws Exception {
        UserDetails userDetails = User.withUsername("user-1")
                .password("senha123").authorities("CLIENT").build();

        when(request.getHeader("Authorization")).thenReturn("Bearer token-valido");
        when(jwtUtil.extrairSubjectUUIDToken("token-valido")).thenReturn("user-1");
        when(userDetailsService.loadUserByUsername("user-1")).thenReturn(userDetails);
        when(jwtUtil.validateToken("token-valido", "user-1")).thenReturn(true);

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication(), notNullValue());
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName(), is("user-1"));
        verify(chain).doFilter(request, response);
    }

    @Test
    void tokenInvalido_retorna401_eNaoSegueACadeia() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token-invalido");
        when(jwtUtil.extrairSubjectUUIDToken("token-invalido"))
                .thenThrow(new MalformedJwtException("token ruim"));
        when(request.getRequestURI()).thenReturn("/orders");
        // getWriter precisa devolver um PrintWriter real para o filtro escrever o JSON de erro
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication(), is(nullValue()));
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(chain, never()).doFilter(request, response);
    }
}
