package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.RegisterAdminInput;
import com.letraaletra.api.features.admin.application.output.RegisterAdminOutput;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.exception.EmailAlreadyInUseException;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PasswordService;

public class RegisterAdminUseCase implements UseCase<RegisterAdminInput, RegisterAdminOutput> {
    private final AdminRepository adminRepository;
    private final PasswordService passwordService;
    private final AdminChecker adminChecker;

    public RegisterAdminUseCase(
            AdminRepository adminRepository,
            PasswordService passwordService,
            AdminChecker adminChecker
    ) {
        this.adminRepository = adminRepository;
        this.passwordService = passwordService;
        this.adminChecker = adminChecker;
    }

    @Override
    public RegisterAdminOutput execute(RegisterAdminInput input) {
        adminChecker.check(input.auth());

        validateEmail(input.email());

        Admin admin = Admin.create(
               input.name(),
               input.email(),
               passwordService.hash(input.password())
        );

        adminRepository.save(admin);

        return buildOutput(admin);
    }

    private void validateEmail(String email) {
        boolean exists = adminRepository.existsByEmail(email);

        if (exists) {
            throw new EmailAlreadyInUseException();
        }
    }

    private RegisterAdminOutput buildOutput(Admin admin) {
        return new RegisterAdminOutput(
                admin
        );
    }
}
