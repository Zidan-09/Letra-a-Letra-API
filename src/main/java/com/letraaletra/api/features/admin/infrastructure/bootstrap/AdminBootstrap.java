package com.letraaletra.api.features.admin.infrastructure.bootstrap;

import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.permission.Permission;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.shared.domain.security.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AdminBootstrap implements CommandLineRunner {

    @Value("${admin.bootstrap.password:}")
    private String bootstrapPassword;

    private final AdminRepository repository;
    private final PasswordService passwordService;

    @Override
    @Transactional
    public void run(String... args) {
        if (repository.count() == 0) {

            Admin admin = Admin.create(
                    "admin",
                    "admin@localhost.com"
            );

            admin.activateAccount(
                    passwordService.hash(bootstrapPassword)
            );

            admin.getPermissions().set(new Permission(
                    PermissionKey.ADMIN,
                    Set.of(PermissionAction.CREATE, PermissionAction.EDIT, PermissionAction.VIEW)
            ));

            admin.promoteSuperAdmin();

            repository.save(admin);
        }
    }
}