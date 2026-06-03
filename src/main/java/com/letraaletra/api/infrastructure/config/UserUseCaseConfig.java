package com.letraaletra.api.infrastructure.config;

import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.application.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserUseCaseConfig {
    @Bean
    public FindUserByIdUseCase findUserByIdUseCase(UserRepository userRepository) {
        return new FindUserByIdUseCase(userRepository);
    }

    @Bean
    public SetAvatarUseCase setAvatarUseCase(UserRepository userRepository) {
        return new SetAvatarUseCase(userRepository);
    }
}
