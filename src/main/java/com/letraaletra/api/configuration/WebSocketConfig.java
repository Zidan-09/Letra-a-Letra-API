package com.letraaletra.api.configuration;

import com.letraaletra.api.infra.websocket.AuthHandshakeInterceptor;
import com.letraaletra.api.infra.websocket.GameWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final GameWebSocketHandler handler;
    private final AuthHandshakeInterceptor interceptor;

    public WebSocketConfig(GameWebSocketHandler handler, AuthHandshakeInterceptor interceptor) {
        this.handler = handler;
        this.interceptor = interceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/game")
                .addInterceptors(interceptor)
                .setAllowedOrigins("*");
    }
}