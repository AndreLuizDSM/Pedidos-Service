package com.andre.pedidosservice.security;

import com.andre.pedidosservice.user.core.UserStatus;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;


// Gera , valida e extrai todos os dados do Token.
@Service
public class JwtUtil {

    // Chave secreta usada para assinar e verificar tokens JWT

    private static final String SECRETKEY = "YXNzaW5hdHVyYS1sb25nYS1wYXJhLWNoYXZlLXRlc3RlLWRvLWNvZGlnbw==";

    public SecretKey getSecretKey(){
        byte[] key = Base64.getDecoder().decode(SECRETKEY);
        return Keys.hmacShaKeyFor(key);
    }

    // Gera um token JWT com o email do usuário e validade de 1 hora
    public String generateToken(String userUUID, UserStatus status) {
        return Jwts.builder()
                .subject(userUUID) // Define o email de usuário como o assunto do token
                .claim("status" , status.name())
                .issuedAt(new Date()) // Define a data e hora de emissão do token
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Define a data e hora de expiração
                // (1 hora a partir da emissão)
                .signWith(getSecretKey()) // Assina o token com a secretkey
                .compact();
    }

    // Extrai as claims do token JWT (informações adicionais do token)
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey()) // Define a chave secreta
                // para validar a assinatura do token
                .build()
                .parseSignedClaims(token) // Analisa o token JWT e obtém as claims
                .getPayload(); // Retorna o corpo das claims
    }

    // Extrai o UUID de usuário do token JWT
    public String extrairSubjectUUIDToken(String token) {
        // Obtém o assunto (nome de usuário) das claims do token
        return extractClaims(token).getSubject();
    }

    // Extrai o STATUS do usuário do token JWT
    public String extrairSTATUSToken(String token) {
        // Obtém o assunto (nome de usuário) das claims do token
        return extractClaims(token).get("status", String.class);
    }

    // Verifica se o token JWT está expirado
    public boolean isTokenExpired(String token) {

        // Compara a data de expiração do token com a data atual
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Valida o token JWT verificando o SUBJECT e se o token não está expirado
    public boolean validateToken(String token, String userID) {
        // Extrai o SUBJECT do token
        final String extractedUsername = extrairSubjectUUIDToken(token);
        // Verifica se o nome de usuário do token corresponde ao fornecido e se o token não está expirado
        return (extractedUsername.equals(userID) && !isTokenExpired(token));
    }
}
