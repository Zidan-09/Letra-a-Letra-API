package com.letraaletra.api.shared.domain.service;

public interface TokenHashService {
    String hash(String token);
    boolean matches(String raw, String hash);
}
