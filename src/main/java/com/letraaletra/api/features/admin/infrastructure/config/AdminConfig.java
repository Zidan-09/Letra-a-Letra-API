package com.letraaletra.api.features.admin.infrastructure.config;

import com.letraaletra.api.features.admin.application.usecase.AuthAdminUseCase;
import com.letraaletra.api.features.admin.application.usecase.GetSystemStatusUseCase;
import com.letraaletra.api.features.admin.application.usecase.RegisterAdminUseCase;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.security.PasswordService;
import com.letraaletra.api.shared.domain.security.TokenService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminConfig {
    @Bean
    public RegisterAdminUseCase registerAdminUseCase(
            AdminRepository adminRepository,
            PasswordService passwordService,
            AdminChecker adminChecker
    ) {
        return new RegisterAdminUseCase(
                adminRepository,
                passwordService,
                adminChecker
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

    @Bean
    public GetSystemStatusUseCase getSystemStatusUseCase(
            MeterRegistry meterRegistry,
            HealthEndpoint healthEndpoint,
            AdminChecker adminChecker
    ) {
        return new GetSystemStatusUseCase(
                meterRegistry,
                healthEndpoint,
                adminChecker
        );
    }
}
