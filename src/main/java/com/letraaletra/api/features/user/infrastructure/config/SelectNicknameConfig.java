package com.letraaletra.api.features.user.infrastructure.config;

import com.letraaletra.api.application.service.SelectNicknameService;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class SelectNicknameConfig {
    @Bean
    public SelectNicknameService selectNicknameService(
            UserRepository userRepository,
            Random random
    ) {
        return new SelectNicknameService(
                userRepository,
                random
        );
    }
}
