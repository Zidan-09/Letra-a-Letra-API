package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.DeleteAdminInput;
import com.letraaletra.api.features.admin.application.output.DeleteAdminOutput;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.exception.AdminNotFoundException;
import com.letraaletra.api.features.admin.domain.exception.InvalidAdminOperationException;
import com.letraaletra.api.features.admin.domain.exception.PermissionDeniedException;
import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class DeleteAdminUseCase implements UseCase<DeleteAdminInput, DeleteAdminOutput> {
    private final AdminRepository adminRepository;
    private final AdminChecker adminChecker;

    public DeleteAdminUseCase(
            AdminRepository adminRepository,
            AdminChecker adminChecker
    ) {
        this.adminRepository = adminRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public DeleteAdminOutput execute(DeleteAdminInput input) {
        adminChecker.check(input.principal(), PermissionKey.ADMIN, PermissionAction.DELETE);

        if (input.adminId().equals(input.principal().auth()))
            throw new InvalidAdminOperationException();

        Admin admin = adminRepository.find(input.adminId())
                .orElseThrow(AdminNotFoundException::new);

        validateAdminDeletion(admin, input);

        adminRepository.delete(admin);

        return new DeleteAdminOutput(admin);
    }

    private void validateAdminDeletion(Admin admin, DeleteAdminInput input) {
        boolean isSuper = input.principal().isSuper();

        if (!isSuper && admin.isSuper()) {
            throw new PermissionDeniedException();
        }
    }
}
