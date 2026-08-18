package com.letraaletra.api.features.user.infrastructure.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.letraaletra.api.features.user.application.output.GoogleAuthData;
import com.letraaletra.api.features.user.application.port.GoogleTokenService;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
@RequiredArgsConstructor
public class GoogleAuthTokenService implements GoogleTokenService {
    private final GoogleIdTokenVerifier verifier;

    @Override
    public GoogleAuthData verify(String token) {
        try {
            GoogleIdToken idToken = verifier.verify(token);

            if (idToken == null) {
                throw new InvalidTokenException();
            }

            return new GoogleAuthData(
                    idToken.getPayload().getEmail(),
                    idToken.getPayload().getSubject()
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new InvalidTokenException();
        }
    }
}
