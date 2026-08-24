package com.letraaletra.api.shared.infrastructure.config;

import com.letraaletra.api.shared.infrastructure.websocket.AuthHandshakeInterceptor;
import com.letraaletra.api.shared.infrastructure.websocket.MainWebSocketEndpoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
public class GameWebSocketConfig implements WebSocketConfigurer {

    private final MainWebSocketEndpoint handler;
    private final AuthHandshakeInterceptor interceptor;

    public GameWebSocketConfig(
            MainWebSocketEndpoint handler,
            AuthHandshakeInterceptor interceptor
    ) {
        this.handler = handler;
        this.interceptor = interceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/game")
                .addInterceptors(interceptor)
                .setAllowedOrigins("*");
    }
}
