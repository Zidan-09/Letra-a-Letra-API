package com.letraaletra.api.shared.infrastructure.service;

import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.exception.AdminNotFoundException;
import com.letraaletra.api.features.admin.domain.exception.PermissionDeniedException;
import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.domain.security.exceptions.UserIsNotAdminException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CheckIfIsAdminService implements AdminChecker {
    private final AdminRepository adminRepository;

    @Override
    public void check(AuthenticatedUser principal, PermissionKey key, PermissionAction action) {
        if (!principal.isAdmin()) throw new UserIsNotAdminException();

        Admin admin = adminRepository.find(principal.auth())
                .orElseThrow(AdminNotFoundException::new);

        if (!admin.getPermissions().can(key, action)) throw new PermissionDeniedException();
    }
}
