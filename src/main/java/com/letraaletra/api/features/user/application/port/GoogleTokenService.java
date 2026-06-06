package com.letraaletra.api.features.user.application.port;

import com.letraaletra.api.features.user.application.output.GoogleAuthData;

public interface GoogleTokenService {
    GoogleAuthData verify(String token);
}
