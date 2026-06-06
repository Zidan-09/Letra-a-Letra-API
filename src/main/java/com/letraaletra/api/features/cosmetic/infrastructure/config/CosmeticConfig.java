package com.letraaletra.api.features.cosmetic.infrastructure.config;

import com.letraaletra.api.features.user.application.usecase.ChangeCosmeticUseCase;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CosmeticConfig {
    @Bean
    public ChangeCosmeticUseCase setAvatarUseCase(UserRepository userRepository) {
        return new ChangeCosmeticUseCase(userRepository);
    }
}
