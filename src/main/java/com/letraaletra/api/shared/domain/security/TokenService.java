package com.letraaletra.api.shared.domain.security;

import java.util.UUID;

public interface TokenService {
    String generateUserToken(UUID id);
    String generateAdminToken(UUID id);
    TokenContent getTokenContent(String token);
}
