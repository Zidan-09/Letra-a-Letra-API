package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.GetMyAdminProfileInput;
import com.letraaletra.api.features.admin.application.output.GetMyAdminProfileOutput;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.exception.AdminNotFoundException;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class GetMyAdminProfileUseCase implements UseCase<GetMyAdminProfileInput, GetMyAdminProfileOutput> {
    private final AdminRepository adminRepository;

    public GetMyAdminProfileUseCase(
            AdminRepository adminRepository
    ) {
        this.adminRepository = adminRepository;
    }

    @Override
    public GetMyAdminProfileOutput execute(GetMyAdminProfileInput input) {
        Admin admin = adminRepository.find(input.id())
                .orElseThrow(AdminNotFoundException::new);

        return new GetMyAdminProfileOutput(admin);
    }
}
