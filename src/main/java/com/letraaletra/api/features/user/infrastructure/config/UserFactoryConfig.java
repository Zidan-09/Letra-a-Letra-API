package com.letraaletra.api.features.user.infrastructure.config;

import com.letraaletra.api.features.user.domain.factory.UserFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserFactoryConfig {
    @Bean
    public UserFactory userFactory() {
        return new UserFactory();
    }
}
