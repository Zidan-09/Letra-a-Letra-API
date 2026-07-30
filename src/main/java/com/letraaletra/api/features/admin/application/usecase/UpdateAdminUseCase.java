package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.UpdateAdminInput;
import com.letraaletra.api.features.admin.application.output.UpdateAdminOutput;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.exception.AdminNotFoundException;
import com.letraaletra.api.features.admin.domain.exception.InvalidAdminOperationException;
import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class UpdateAdminUseCase implements UseCase<UpdateAdminInput, UpdateAdminOutput> {
    private final AdminRepository adminRepository;
    private final AdminChecker adminChecker;

    public UpdateAdminUseCase(
            AdminRepository adminRepository,
            AdminChecker adminChecker
    ) {
        this.adminRepository = adminRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public UpdateAdminOutput execute(UpdateAdminInput input) {
        adminChecker.check(input.principal(), PermissionKey.ADMIN, PermissionAction.EDIT);

        if (input.adminId().equals(input.principal().auth()))
            throw new InvalidAdminOperationException();

        Admin admin = adminRepository.find(input.adminId())
                .orElseThrow(AdminNotFoundException::new);

        admin.setName(input.name());
        admin.setEmail(input.email());

        input.permissions().forEach(permission -> admin.getPermissions().set(permission));

        return new UpdateAdminOutput(admin);
    }
}
