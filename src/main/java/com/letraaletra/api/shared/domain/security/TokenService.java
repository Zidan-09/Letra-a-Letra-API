package com.letraaletra.api.shared.domain.security;

import java.util.UUID;

public interface TokenService {
    String generateUserToken(UUID id, int tokenVersion);
    String generateAdminToken(UUID id, int tokenVersion);
    TokenContent getTokenContent(String token);
}
