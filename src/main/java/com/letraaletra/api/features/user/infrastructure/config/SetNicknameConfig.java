package com.letraaletra.api.features.user.infrastructure.config;

import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.application.usecase.UpdateNicknameUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SetNicknameConfig {
    @Bean
    public UpdateNicknameUseCase setNicknameUseCase(
            UserRepository userRepository
    ) {
        return new UpdateNicknameUseCase(
                userRepository
        );
    }
}
