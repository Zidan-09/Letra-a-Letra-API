package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.GetAdminsInput;
import com.letraaletra.api.features.admin.application.output.GetAdminsOutput;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.AdminsPage;
import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.data.domain.Page;

public class GetAdminsUseCase implements UseCase<GetAdminsInput, GetAdminsOutput> {
    private final AdminRepository adminRepository;
    private final AdminChecker adminChecker;

    public GetAdminsUseCase(
            AdminRepository adminRepository,
            AdminChecker adminChecker
    ) {
        this.adminRepository = adminRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public GetAdminsOutput execute(GetAdminsInput input) {
        adminChecker.check(input.principal(), PermissionKey.ADMIN, PermissionAction.VIEW);

        Page<Admin> admins = adminRepository.getAdmins(new AdminsPage(
                input.page(),
                input.size(),
                input.sort()
        ));

        return new GetAdminsOutput(admins);
    }
}
