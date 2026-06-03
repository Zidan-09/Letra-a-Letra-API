package com.letraaletra.api.features.user.infrastructure.config;

import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.domain.security.PasswordService;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.features.user.application.usecase.SignInUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignInConfig {
    @Bean
    public SignInUseCase signInUseCase(
            UserRepository userRepository,
            PasswordService passwordService,
            TokenService tokenService
    ) {
        return new SignInUseCase(
                userRepository,
                passwordService,
                tokenService
        );
    }
}
