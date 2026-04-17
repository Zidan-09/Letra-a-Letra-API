package com.letraaletra.api.infrastructure.config;

import com.letraaletra.api.application.usecase.auth.AuthUseCase;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.security.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthUseCaseConfig {
    @Bean
    public AuthUseCase authUseCase(
            TokenService tokenService,
            UserRepository userRepository
    ) {
        return new AuthUseCase(
                tokenService,
                userRepository
        );
    }
}
