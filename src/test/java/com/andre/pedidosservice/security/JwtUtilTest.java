package com.andre.pedidosservice.security;

import com.andre.pedidosservice.user.core.UserStatus;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class JwtUtilTest {

    JwtUtil jwtUtil;

    String token;

    @BeforeEach
    public void setup() {
        jwtUtil = new JwtUtil();
        // token recém-gerado para "user-1" com status CLIENT, usado na maioria dos testes
        token = jwtUtil.generateToken("user-1", UserStatus.CLIENT);
    }

    @Test
    void should_ReturnSubject_when_ExtractFromToken(){
        assertThat(jwtUtil.extrairSubjectUUIDToken(token), is("user-1"));
    }

    @Test
    void should_ReturnStatus_when_ExtractFromToken(){
        assertThat(jwtUtil.extrairSTATUSToken(token), is("CLIENT"));
    }

    @Test
    void should_ReturnTrue_when_TokenHasCorrectId(){
        assertTrue(jwtUtil.validateToken(token, "user-1"));
    }

    @Test
    void should_ReturnFalse_when_TokenHasDifferentId(){
        assertFalse(jwtUtil.validateToken(token, "outro-id"));
    }

    @Test
    void should_ReturnFalse_when_TokenIsRecentlyGenerated(){
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    void should_ThrowException_when_TokenIsMalformed(){
        assertThrows(MalformedJwtException.class,
                () -> jwtUtil.extractClaims("token-invalido"));
    }
}
