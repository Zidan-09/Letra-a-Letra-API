package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.VerifyResetCodeInput;
import com.letraaletra.api.features.user.domain.PasswordResetCode;
import com.letraaletra.api.features.user.domain.repository.reset.ResetCodeRepository;
import com.letraaletra.api.shared.domain.service.TokenHashService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidTokenException;
import org.springframework.transaction.annotation.Transactional;

public class VerifyResetCodeUseCase implements UseCase<VerifyResetCodeInput, Void> {
    private final ResetCodeRepository codeRepository;
    private final TokenHashService tokenHashService;

    public VerifyResetCodeUseCase(
            ResetCodeRepository codeRepository,
            TokenHashService tokenHashService
    ) {
        this.codeRepository = codeRepository;
        this.tokenHashService = tokenHashService;
    }

    @Override
    @Transactional
    public Void execute(VerifyResetCodeInput input) {
        String codeHash = tokenHashService.hash(input.code());

        PasswordResetCode resetCode =
                codeRepository.findByCodeHash(codeHash)
                        .orElseThrow(InvalidTokenException::new);

        resetCode.validate(codeHash);

        codeRepository.save(resetCode);

        return null;
    }
}
