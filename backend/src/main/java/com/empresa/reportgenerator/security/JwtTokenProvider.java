package com.empresa.reportgenerator.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Provedor de tokens JWT.
 * Responsável por gerar, validar e extrair informações de tokens JWT.
 */
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpiration;

    /**
     * Gera um token JWT para o usuário autenticado.
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String jti = UUID.randomUUID().toString();

        // Nova API do jjwt 0.12.x — usa secretKeyFor em vez de Keys.hmacShaKeyFor
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(username)                       // Nova API: subject() em vez de setSubject()
                .claim("roles", roles)
                .id(jti)                                 // Nova API: id() em vez de setId()
                .issuedAt(now)                           // Nova API: issuedAt() em vez de setIssuedAt()
                .expiration(expiryDate)                  // Nova API: expiration() em vez de setExpiration()
                .signWith(key)                           // Nova API: signWith(key) sem algoritmo explícito
                .compact();
    }

    /**
     * Extrai o username do token JWT.
     */
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * Extrai as roles do token JWT.
     */
    public String getRolesFromToken(String token) {
        return getClaimsFromToken(token).get("roles", String.class);
    }

    /**
     * Extrai o JTI (JWT ID) do token.
     */
    public String getJtiFromToken(String token) {
        return getClaimsFromToken(token).getId();
    }

    /**
     * Valida se o token é válido (assinatura correta e não expirado).
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Extrai os claims do token JWT.
     */
    private Claims getClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        // Nova API do jjwt 0.12.x — usa parser() em vez de parserBuilder()
        return Jwts.parser()
                .verifyWith(key)                         // Nova API: verifyWith() em vez de setSigningKey()
                .build()
                .parseSignedClaims(token)                // Nova API: parseSignedClaims() em vez de parseClaimsJws()
                .getPayload();                           // Nova API: getPayload() em vez de getBody()
    }
}
