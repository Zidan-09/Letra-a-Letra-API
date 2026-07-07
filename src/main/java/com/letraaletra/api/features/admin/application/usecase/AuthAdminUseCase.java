package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.AuthAdminInput;
import com.letraaletra.api.features.admin.application.output.AuthAdminOutput;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.exception.AdminNotFoundException;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PasswordService;
import com.letraaletra.api.shared.domain.security.TokenService;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidPasswordException;

import java.util.UUID;

public class AuthAdminUseCase implements UseCase<AuthAdminInput, AuthAdminOutput> {
    private final AdminRepository adminRepository;
    private final PasswordService passwordService;
    private final TokenService tokenService;


    public AuthAdminUseCase(
            AdminRepository adminRepository,
            PasswordService passwordService,
            TokenService tokenService
    ) {
        this.adminRepository = adminRepository;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
    }

    @Override
    public AuthAdminOutput execute(AuthAdminInput input) {
        Admin admin = adminRepository.findByEmail(input.email())
                .orElseThrow(AdminNotFoundException::new);

        checkMatch(input.password(), admin.getHashPassword());

        String token = tokenService.generateAdminToken(admin.getId());

        return buildOutput(admin.getId(), token);
    }

    private void checkMatch(String password, String hash) {
        boolean matches = passwordService.matches(password, hash);

        if (!matches) {
            throw new InvalidPasswordException();
        }
    }

    private AuthAdminOutput buildOutput(UUID id, String token) {
        return new AuthAdminOutput(
                id,
                token
        );
    }
}
