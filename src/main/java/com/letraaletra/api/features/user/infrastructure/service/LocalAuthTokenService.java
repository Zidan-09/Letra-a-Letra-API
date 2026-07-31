package com.letraaletra.api.features.user.infrastructure.service;

import com.letraaletra.api.features.user.application.output.GoogleAuthData;
import com.letraaletra.api.features.user.application.port.GoogleTokenService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Profile("!prod")
public class LocalAuthTokenService implements GoogleTokenService {
    @Override
    public GoogleAuthData verify(String token) {
        return new GoogleAuthData(
                "localgoogleuser@gmail.com",
                UUID.randomUUID().toString()
        );
    }
}
