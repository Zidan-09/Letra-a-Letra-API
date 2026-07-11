package com.letraaletra.api.shared.infrastructure.config;

import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.exception.AdminNotFoundException;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.domain.security.TokenContent;
import com.letraaletra.api.shared.domain.security.TokenService;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization != null && authorization.startsWith("Bearer ")) {

            String token = authorization.substring(7);

            try {
                TokenContent content = tokenService.getTokenContent(token);

                Authentication authentication;

                switch (content.role()) {
                    case USER -> {
                        User user = userRepository.find(content.id())
                                .orElseThrow(UserNotFoundException::new);

                        authentication = new UsernamePasswordAuthenticationToken(
                            new AuthenticatedUser(user.getId(), user.getNickname(), false),
                            null,
                            Collections.emptyList()
                        );
                    }
                    case ADMIN -> {
                        Admin admin = adminRepository.find(content.id())
                                .orElseThrow(AdminNotFoundException::new);

                        authentication = new UsernamePasswordAuthenticationToken(
                            new AuthenticatedUser(admin.getId(), admin.getName(), true),
                            null,
                            Collections.emptyList()
                        );
                    }

                    case null, default -> throw new InvalidTokenException();
                }

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

            } catch (Exception ignored) {
            }
        }

        filterChain.doFilter(request, response);
    }
}