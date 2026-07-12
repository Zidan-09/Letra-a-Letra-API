package com.letraaletra.api.features.admin.infrastructure.websocket;

import com.letraaletra.api.shared.domain.security.Roles;
import com.letraaletra.api.shared.domain.security.TokenContent;
import com.letraaletra.api.shared.domain.security.TokenService;
import org.jspecify.annotations.NonNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class AuthHandshakeAdminInterceptor implements HandshakeInterceptor {
    private final TokenService tokenService;

    public AuthHandshakeAdminInterceptor(
            TokenService tokenService
    ) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            @NonNull Map<String, Object> attributes
    ) {
        UriComponents uri = UriComponentsBuilder.fromUri(request.getURI()).build();
        String token = uri.getQueryParams().getFirst("token");

        TokenContent content = tokenService.getTokenContent(token);

        if (content == null || !content.role().equals(Roles.ADMIN)) {
            return false;
        }

        attributes.put("adminId", content.id().toString());

        return true;
    }

    @Override
    public void afterHandshake(
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            Exception exception
    ) {}
}
