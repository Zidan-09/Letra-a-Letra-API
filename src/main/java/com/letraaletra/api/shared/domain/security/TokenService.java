package com.letraaletra.api.shared.domain.security;

import java.util.UUID;

public interface TokenService {
    String generateToken(UUID id, boolean isAdmin);
    UUID getTokenContent(String token);
}
