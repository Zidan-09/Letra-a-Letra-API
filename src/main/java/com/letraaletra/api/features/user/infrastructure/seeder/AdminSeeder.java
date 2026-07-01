package com.letraaletra.api.features.user.infrastructure.seeder;

import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.factory.UserFactory;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.domain.security.PasswordService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class AdminSeeder {
    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final UserFactory userFactory;

    public AdminSeeder(
            UserRepository userRepository,
            PasswordService passwordService,
            UserFactory userFactory
    ) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.userFactory = userFactory;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seed() {
        User admin = userFactory.createLocal(
                "admin",
                "admin@email.com",
                passwordService.hash("admin123")
        );

        admin.setAdmin();

        userRepository.save(admin);
    }
}
