package com.letraaletra.api.features.admin.infrastructure.config;

import com.letraaletra.api.features.admin.infrastructure.websocket.AdminWebSocketHandler;
import com.letraaletra.api.features.admin.infrastructure.websocket.AuthHandshakeAdminInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Arrays;

@Configuration
public class AdminWebSocketConfig implements WebSocketConfigurer {

    @Value("${cors.allowed-origins:*}")
    private String allowedOrigins;

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
        var registration = registry.addHandler(handler, "/ws/admin")
                .addInterceptors(interceptor);
        if ("*".equals(allowedOrigins.trim())) {
            registration.setAllowedOriginPatterns("*");
        } else {
            registration.setAllowedOriginPatterns(
                    Arrays.stream(allowedOrigins.split(",")).map(String::trim).toArray(String[]::new)
            );
        }
    }
}
