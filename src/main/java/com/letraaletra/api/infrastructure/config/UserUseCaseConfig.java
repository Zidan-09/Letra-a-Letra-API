package com.letraaletra.api.features.participant.infrastructure.config;

import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.application.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserUseCaseConfig {
    @Bean
    public SetAvatarUseCase setAvatarUseCase(UserRepository userRepository) {
        return new SetAvatarUseCase(userRepository);
    }
}
