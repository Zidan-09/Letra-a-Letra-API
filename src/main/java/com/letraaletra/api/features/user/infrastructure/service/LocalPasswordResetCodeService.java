package com.letraaletra.api.features.user.infrastructure.service;

import com.letraaletra.api.features.user.application.port.ResetCodeService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("test")
public class LocalPasswordResetCodeService implements ResetCodeService {

    @Override
    public String generate() {
        return "123456";
    }
}
