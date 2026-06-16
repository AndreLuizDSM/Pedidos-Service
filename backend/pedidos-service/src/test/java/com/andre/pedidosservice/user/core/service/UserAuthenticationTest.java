package com.andre.pedidosservice.user.core.service;

import com.andre.pedidosservice.user.core.UserStatus;
import com.andre.pedidosservice.user.core.domain.UserDomain;
import com.andre.pedidosservice.user.gateways.out.UserAuthenticationGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAuthenticationTest {

    @InjectMocks
    private UserAuthentication userAuthentication;

    @Mock
    private UserAuthenticationGateway authentication;

    private UserDomain userDomain;

    @BeforeEach
    void setup() {
        userDomain = new UserDomain("André", "andre@email.com", "senha123", "123", UserStatus.CLIENT);
    }

                        // login

    @Test
    void should_ReturnToken_when_LoginDelegatesToGateway() {
        when(authentication.userLogin(userDomain)).thenReturn("Bearer token-valido");

        String result = userAuthentication.login(userDomain);

        assertNotNull(result);
        assertEquals("Bearer token-valido", result);

        // A service apenas delega para o gateway de autenticação
        verify(authentication).userLogin(userDomain);
        verifyNoMoreInteractions(authentication);
    }
}
