package com.letraaletra.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(tokenService, "secret", "fsdifsfus62354723hgjhgdf¨%#&¨#$gyg$#");
    }

    @Test
    @DisplayName("should return a id from a json web token")
    void getTokenContent() {
        String id = "abacate123";

        String token = tokenService.generateToken(id);

        String returnedId = tokenService.getTokenContent(token);

        assertEquals(id, returnedId);
    }
}