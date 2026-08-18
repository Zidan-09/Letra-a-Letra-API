package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.FindAdminByEmailInput;
import com.letraaletra.api.features.admin.application.output.FindAdminByEmailOutput;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.exception.AdminNotFoundException;
import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class FindAdminByEmailUseCase implements UseCase<FindAdminByEmailInput, FindAdminByEmailOutput> {
    private final AdminRepository adminRepository;
    private final AdminChecker adminChecker;

    public FindAdminByEmailUseCase(
            AdminRepository adminRepository,
            AdminChecker adminChecker
    ) {
        this.adminRepository = adminRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public FindAdminByEmailOutput execute(FindAdminByEmailInput input) {
        adminChecker.check(input.principal(), PermissionKey.ADMIN, PermissionAction.VIEW);

        Admin admin = adminRepository.findByEmail(input.email())
                .orElseThrow(AdminNotFoundException::new);

        return new FindAdminByEmailOutput(admin);
    }
}
