package com.letraaletra.api.features.user.infrastructure.config;

import com.letraaletra.api.application.service.SelectNicknameService;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.domain.security.PasswordService;
import com.letraaletra.api.features.user.application.usecase.CreateUserUseCase;
import com.letraaletra.api.features.user.domain.factory.UserFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CreateUserConfig {
    @Bean
    public CreateUserUseCase createUserUseCase(
            UserRepository userRepository,
            PasswordService passwordService,
            UserFactory userFactory,
            SelectNicknameService selectNicknameService
    ) {
        return new CreateUserUseCase(
                userRepository,
                passwordService,
                userFactory,
                selectNicknameService
        );
    }
}
