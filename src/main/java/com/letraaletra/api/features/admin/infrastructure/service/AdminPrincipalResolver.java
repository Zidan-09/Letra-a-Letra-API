package com.letraaletra.api.features.admin.infrastructure.service;

import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.exception.AdminNotFoundException;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.domain.exception.SessionExpiredException;
import com.letraaletra.api.shared.domain.security.RolePrincipalResolver;
import com.letraaletra.api.shared.domain.security.Roles;
import com.letraaletra.api.shared.domain.security.TokenContent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminPrincipalResolver implements RolePrincipalResolver {
    private final AdminRepository adminRepository;

    @Override
    public Roles role() {
        return Roles.ADMIN;
    }

    @Override
    public AuthenticatedUser resolve(TokenContent content) {
        Admin admin = adminRepository.find(content.id())
                .orElseThrow(AdminNotFoundException::new);

        if (!admin.getTokenVersion().equals(content.tokenVersion())) {
            throw new SessionExpiredException();
        }

        return new AuthenticatedUser(admin.getId(), admin.getName(), true, admin.isSuper());
    }
}
