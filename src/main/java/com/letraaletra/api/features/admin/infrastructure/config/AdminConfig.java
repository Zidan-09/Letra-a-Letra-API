package com.letraaletra.api.features.admin.infrastructure.config;

import com.letraaletra.api.features.admin.application.port.HealthChecker;
import com.letraaletra.api.features.admin.application.port.MeterChecker;
import com.letraaletra.api.features.admin.application.usecase.AuthAdminUseCase;
import com.letraaletra.api.features.admin.application.service.GetApplicationStatusService;
import com.letraaletra.api.features.admin.application.service.GetSystemStatusService;
import com.letraaletra.api.features.admin.application.usecase.RegisterAdminUseCase;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.user.application.port.SessionRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.security.PasswordService;
import com.letraaletra.api.shared.domain.security.TokenService;
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
    public GetSystemStatusService getSystemStatusUseCase(
            MeterChecker meterChecker,
            HealthChecker healthChecker
    ) {
        return new GetSystemStatusService(
                meterChecker,
                healthChecker
        );
    }

    @Bean
    public GetApplicationStatusService getApplicationStatusUseCase(
            UserRepository userRepository,
            SessionRepository sessionRepository,
            ActorManager<Game> actorManager
    ) {
        return new GetApplicationStatusService(
                userRepository,
                sessionRepository,
                actorManager
        );
    }
}
