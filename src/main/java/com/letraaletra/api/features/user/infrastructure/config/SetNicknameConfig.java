package com.letraaletra.api.features.user.infrastructure.config;

import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.application.usecase.SetNicknameUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SetNicknameConfig {
    @Bean
    public SetNicknameUseCase setNicknameUseCase(
            UserRepository userRepository
    ) {
        return new SetNicknameUseCase(
                userRepository
        );
    }
}
