package com.letraaletra.api.shared.domain.security;

import java.util.UUID;

public interface TokenService {
    String generateUserToken(UUID id, UUID tokenVersion);
    String generateAdminToken(UUID id, UUID tokenVersion);
    TokenContent getTokenContent(String token);
}
