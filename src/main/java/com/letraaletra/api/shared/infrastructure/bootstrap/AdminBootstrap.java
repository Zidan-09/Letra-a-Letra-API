package com.letraaletra.api.shared.infrastructure.bootstrap;

import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.shared.domain.security.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminBootstrap implements CommandLineRunner {

    private final AdminRepository repository;
    private final PasswordService passwordService;

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            repository.save(Admin.create(
                    "admin",
                    "admin@localhost.com",
                    passwordService.hash("lalAdmin2026")
            ));
        }
    }
}