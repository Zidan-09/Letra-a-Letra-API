package com.letraaletra.api.features.admin.infrastructure.bootstrap;

import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.permission.Permission;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.features.admin.domain.AdminsPage;
import com.letraaletra.api.shared.domain.security.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AdminBootstrap implements CommandLineRunner {

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
                    passwordService.hash("lalAdmin2026")
            );

            admin.getPermissions().set(new Permission(
                    PermissionKey.ADMIN,
                    Set.of(PermissionAction.CREATE, PermissionAction.EDIT, PermissionAction.VIEW)
            ));

            admin.promoteSuperAdmin();

            repository.save(admin);
        }

        grantAuditPermissionToExistingAdmins();
    }

    private void grantAuditPermissionToExistingAdmins() {
        int page = 0;
        Page<Admin> adminsPage;

        do {
            adminsPage = repository.getAdmins(new AdminsPage(page, 100, Sort.unsorted()));

            adminsPage.forEach(admin -> {
                if (admin.getPermissions().can(PermissionKey.AUDIT, PermissionAction.VIEW)) {
                    return;
                }

                admin.getPermissions().set(new Permission(
                        PermissionKey.AUDIT,
                        Set.of(PermissionAction.VIEW)
                ));

                repository.save(admin);
            });

            page++;
        } while (page < adminsPage.getTotalPages());
    }
}