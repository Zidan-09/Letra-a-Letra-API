package com.letraaletra.api.shared.infrastructure.bootstrap;

import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.permission.Permission;
import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.shared.domain.security.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AdminBootstrap implements CommandLineRunner {

    private final AdminRepository repository;
    private final PasswordService passwordService;

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {

            Admin admin = Admin.create(
                    "admin",
                    "admin@localhost.com",
                    passwordService.hash("lalAdmin2026")
            );

            admin.getPermissions().set(new Permission(
                    PermissionKey.ADMIN,
                    Set.of(PermissionAction.CREATE)
            ));

            repository.save(admin);
        }
    }
}