package com.letraaletra.api.shared.infrastructure.websocket;

import com.letraaletra.api.shared.domain.security.Roles;
import com.letraaletra.api.shared.domain.security.TokenContent;
import com.letraaletra.api.shared.domain.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthHandshakeInterceptor — caracterização da autenticação de handshake")
class AuthHandshakeInterceptorTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private WebSocketHandler wsHandler;

    private final Map<String, Object> attributes = new HashMap<>();

    private AuthHandshakeInterceptor interceptor;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        interceptor = new AuthHandshakeInterceptor(tokenService);
        lenient().when(request.getURI()).thenReturn(URI.create("ws://localhost:8080/ws/game?token=" + userId));
    }

    @Test
    @DisplayName("token válido popula userId nos atributos e permite o handshake")
    void shouldAcceptValidToken() {
        when(tokenService.getTokenContent(userId.toString()))
                .thenReturn(new TokenContent(userId, Roles.USER, UUID.randomUUID()));

        boolean accepted = interceptor.beforeHandshake(request, response, wsHandler, attributes);

        assertTrue(accepted);
        assertEquals(userId.toString(), attributes.get("userId"));
    }

    @Test
    @DisplayName("token inválido recusa o handshake sem popular atributos")
    void shouldRejectInvalidToken() {
        when(tokenService.getTokenContent(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new RuntimeException("invalid token"));

        boolean accepted = interceptor.beforeHandshake(request, response, wsHandler, attributes);

        assertFalse(accepted);
        assertTrue(attributes.isEmpty());
    }
}
