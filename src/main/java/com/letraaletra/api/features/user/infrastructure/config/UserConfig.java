package com.letraaletra.api.features.user.infrastructure.config;

import com.letraaletra.api.features.user.application.usecase.GetUserInventoryUseCase;
import com.letraaletra.api.features.user.domain.repository.InventoryRepository;
import com.letraaletra.api.shared.domain.security.PasswordService;
import com.letraaletra.api.shared.domain.security.TokenService;
import com.letraaletra.api.features.user.application.service.SelectNicknameService;
import com.letraaletra.api.features.user.application.usecase.CreateUserUseCase;
import com.letraaletra.api.features.user.application.usecase.SignInUseCase;
import com.letraaletra.api.features.user.application.usecase.ChangeNicknameUseCase;
import com.letraaletra.api.features.user.domain.factory.UserFactory;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class UserConfig {
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

    @Bean
    public SelectNicknameService selectNicknameService(
            UserRepository userRepository
    ) {
        return new SelectNicknameService(
                userRepository,
                new Random()
        );
    }

    @Bean
    public ChangeNicknameUseCase setNicknameUseCase(
            UserRepository userRepository
    ) {
        return new ChangeNicknameUseCase(
                userRepository
        );
    }

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

    @Bean
    public UserFactory userFactory() {
        return new UserFactory();
    }

    @Bean
    public GetUserInventoryUseCase getUserInventoryUseCase(
            InventoryRepository inventoryRepository
    ) {
        return new GetUserInventoryUseCase(
                inventoryRepository
        );
    }
}
