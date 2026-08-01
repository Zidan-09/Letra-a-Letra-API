package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.RegisterAdminInput;
import com.letraaletra.api.features.admin.application.output.RegisterAdminOutput;
import com.letraaletra.api.features.admin.application.port.AdminInvitationEmailService;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.AdminPasswordSetupToken;
import com.letraaletra.api.features.admin.domain.TokenHashService;
import com.letraaletra.api.features.admin.domain.exception.EmailAlreadyInUseException;
import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.admin.domain.repository.AdminTokenRepository;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

public class RegisterAdminUseCase implements UseCase<RegisterAdminInput, RegisterAdminOutput> {
    private final AdminRepository adminRepository;
    private final TokenHashService tokenHashService;
    private final AdminTokenRepository tokenRepository;
    private final AdminInvitationEmailService emailService;
    private final AdminChecker adminChecker;

    public RegisterAdminUseCase(
            AdminRepository adminRepository,
            TokenHashService tokenHashService,
            AdminTokenRepository tokenRepository,
            AdminInvitationEmailService emailService,
            AdminChecker adminChecker
    ) {
        this.adminRepository = adminRepository;
        this.tokenHashService = tokenHashService;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.adminChecker = adminChecker;
    }

    @Override
    @Transactional
    public RegisterAdminOutput execute(RegisterAdminInput input) {
        adminChecker.check(input.principal(), PermissionKey.ADMIN, PermissionAction.CREATE);

        validateEmail(input.email());

        Admin admin = Admin.create(
               input.name(),
               input.email()
        );

        String token = UUID.randomUUID().toString();

        String tokenHash = tokenHashService.hash(token);

        AdminPasswordSetupToken adminPasswordSetupToken = AdminPasswordSetupToken.create(
                tokenHash,
                admin.getId(),
                LocalDateTime.now().plusDays(2)
        );

        adminRepository.save(admin);
        tokenRepository.save(adminPasswordSetupToken);

        emailService.send(
                admin.getEmail(),
                admin.getName(),
                token
        );

        return new RegisterAdminOutput(admin);
    }

    private void validateEmail(String email) {
        boolean exists = adminRepository.existsByEmail(email);

        if (exists) {
            throw new EmailAlreadyInUseException();
        }
    }
}
