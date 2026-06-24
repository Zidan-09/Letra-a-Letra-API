package com.letraaletra.api.shared.domain.security;

import java.util.UUID;

public interface TokenService {
    String generateToken(UUID id);
    UUID getTokenContent(String token);
}
