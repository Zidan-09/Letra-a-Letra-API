package com.letraaletra.api.shared.application.service;

import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.security.exceptions.UserIsNotAdminException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CheckIfIsAdminService implements AdminChecker {
    private final AdminRepository adminRepository;

    public CheckIfIsAdminService(
            AdminRepository adminRepository
    ) {
        this.adminRepository = adminRepository;
    }

    @Override
    public void check(UUID id) {
        adminRepository.find(id)
                .orElseThrow(UserIsNotAdminException::new);
    }
}
