package com.letraaletra.api.shared.domain.security;

public interface TokenService {
    String generateToken(String id);
    String getTokenContent(String token);
}
