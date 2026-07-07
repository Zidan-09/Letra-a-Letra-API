package com.letraaletra.api.shared.infrastructure.security;

import com.letraaletra.api.shared.domain.security.Roles;
import com.letraaletra.api.shared.domain.security.TokenContent;
import com.letraaletra.api.shared.domain.security.TokenService;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidTokenException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class JsonWebTokenService implements TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateUserToken(UUID id) {
        return Jwts.builder()
                .subject(id.toString())
                .claim("role", Roles.USER.name())
                .issuedAt(new java.util.Date())
                .expiration(new java.util.Date(System.currentTimeMillis() + (6 * 60 * 60 * 1000L)))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String generateAdminToken(UUID id) {
        return Jwts.builder()
                .subject(id.toString())
                .claim("role", Roles.ADMIN.name())
                .issuedAt(new java.util.Date())
                .expiration(new java.util.Date(System.currentTimeMillis() + (6 * 60 * 60 * 1000L)))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public TokenContent getTokenContent(String token) {
        try {
            var claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            UUID id = UUID.fromString(claims.getSubject());
            Roles role = Roles.valueOf(claims.get("role", String.class));

            return new TokenContent(id, role);

        } catch (Exception ex) {
            throw new InvalidTokenException();
        }
    }
}
