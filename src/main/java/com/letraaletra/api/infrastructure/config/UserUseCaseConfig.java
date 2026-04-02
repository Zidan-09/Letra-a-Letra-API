package com.letraaletra.api.infrastructure.config;

import com.letraaletra.api.application.usecase.user.CreateUserUseCase;
import com.letraaletra.api.application.usecase.user.FindUserByIdUseCase;
import com.letraaletra.api.application.usecase.user.SignInUseCase;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.security.PasswordService;
import com.letraaletra.api.domain.security.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserUseCaseConfig {
    @Bean
    public CreateUserUseCase createUserUseCase(UserRepository userRepository, PasswordService passwordService) {
        return new CreateUserUseCase(userRepository, passwordService);
    }

    @Bean
    public FindUserByIdUseCase findUserByIdUseCase(UserRepository userRepository) {
        return new FindUserByIdUseCase(userRepository);
    }

    @Bean
    public SignInUseCase signInUseCase(UserRepository userRepository, PasswordService passwordService, TokenService tokenService) {
        return new SignInUseCase(userRepository, passwordService, tokenService);
    }
}
