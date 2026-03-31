package com.letraaletra.api.application.user.service;

import com.letraaletra.api.infra.service.GlobalTokenService;
import com.letraaletra.api.domain.security.exceptions.InvalidTokenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class GlobalTokenServiceTest {

    @InjectMocks
    private GlobalTokenService globalTokenService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(
                globalTokenService,
                "secret",
                "my-super-secret-key-with-at-least-32-chars!!"
        );
    }

    @Test
    @DisplayName("Should generate a jwt")
    void generateToken() {
        String id = "id";

        String token = globalTokenService.generateToken(id);

        Assertions.assertNotNull(token);
    }

    @Test
    @DisplayName("Should return the jwt content")
    void getTokenContent() {
        String id = "id";

        String token = globalTokenService.generateToken(id);

        String tokenContent = globalTokenService.getTokenContent(token);

        Assertions.assertNotNull(tokenContent);
    }

    @Test
    @DisplayName("Should match the id with the token content")
    void match() {
        String id = "id";

        String token = globalTokenService.generateToken(id);

        String tokenContent = globalTokenService.getTokenContent(token);

        Assertions.assertEquals(id, tokenContent);
    }

    @Test
    @DisplayName("Should throw an InvalidTokenException")
    void notGetTokenContent() {
        String fakeToken = "fake-token";

        Assertions.assertThrows(InvalidTokenException.class, () -> globalTokenService.getTokenContent(fakeToken));
    }
}