package com.letraaletra.api.shared.infrastructure.config;

import com.letraaletra.api.features.admin.infrastructure.websocket.AdminWebSocketHandler;
import com.letraaletra.api.features.admin.infrastructure.websocket.AuthHandshakeAdminInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
public class AdminWebSocketConfig implements WebSocketConfigurer {

    private final AdminWebSocketHandler handler;
    private final AuthHandshakeAdminInterceptor interceptor;

    public AdminWebSocketConfig(
            AdminWebSocketHandler handler,
            AuthHandshakeAdminInterceptor interceptor
    ) {
        this.handler = handler;
        this.interceptor = interceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/admin")
                .addInterceptors(interceptor)
                .setAllowedOrigins("*");
    }
}
