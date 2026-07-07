package com.letraaletra.api.features.admin.infrastructure.config;

import com.letraaletra.api.features.admin.application.usecase.AuthAdminUseCase;
import com.letraaletra.api.features.admin.application.usecase.RegisterAdminUseCase;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.shared.domain.security.PasswordService;
import com.letraaletra.api.shared.domain.security.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminConfig {
    @Bean
    public RegisterAdminUseCase registerAdminUseCase(
            AdminRepository adminRepository,
            PasswordService passwordService
    ) {
        return new RegisterAdminUseCase(
                adminRepository,
                passwordService
        );
    }

    @Bean
    public AuthAdminUseCase authAdminUseCase(
            AdminRepository adminRepository,
            PasswordService passwordService,
            TokenService tokenService
    ) {
        return new AuthAdminUseCase(
                adminRepository,
                passwordService,
                tokenService
        );
    }
}
