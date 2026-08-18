package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.ActivateAccountInput;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.AdminPasswordSetupToken;
import com.letraaletra.api.shared.domain.service.TokenHashService;
import com.letraaletra.api.features.admin.domain.exception.AdminNotFoundException;
import com.letraaletra.api.features.admin.domain.repository.AdminTokenRepository;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PasswordService;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidTokenException;

public class ActivateAccountUseCase implements UseCase<ActivateAccountInput, Void> {
    private final TokenHashService tokenHashService;
    private final AdminRepository adminRepository;
    private final AdminTokenRepository tokenRepository;
    private final PasswordService passwordService;

    public ActivateAccountUseCase(
            TokenHashService tokenHashService,
            AdminRepository adminRepository,
            AdminTokenRepository tokenRepository,
            PasswordService passwordService
    ) {
        this.tokenHashService = tokenHashService;
        this.adminRepository = adminRepository;
        this.tokenRepository = tokenRepository;
        this.passwordService = passwordService;
    }

    @Override
    public Void execute(ActivateAccountInput input) {
        String tokenHash = tokenHashService.hash(input.token());

        AdminPasswordSetupToken setupToken = tokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(InvalidTokenException::new);

        setupToken.validate();

        Admin admin = adminRepository.find(setupToken.getAdminId())
                .orElseThrow(AdminNotFoundException::new);

        admin.activateAccount(passwordService.hash(
                input.password()
        ));

        setupToken.markAsUsed();

        adminRepository.save(admin);
        tokenRepository.save(setupToken);

        return null;
    }
}
