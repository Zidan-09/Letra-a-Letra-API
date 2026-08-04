package com.letraaletra.api.features.user.infrastructure.service;

import com.letraaletra.api.features.user.application.port.ResetCodeService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class PasswordResetCodeService implements ResetCodeService {
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generate() {
        int code = secureRandom.nextInt(1_000_000);
        return String.format("%06d", code);
    }
}
