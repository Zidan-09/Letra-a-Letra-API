package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.VerifyResetCodeInput;
import com.letraaletra.api.features.user.domain.PasswordResetCode;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.repository.ResetCodeRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.domain.service.TokenHashService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidTokenException;
import org.springframework.transaction.annotation.Transactional;

public class VerifyResetCodeUseCase implements UseCase<VerifyResetCodeInput, Void> {
    private final UserRepository userRepository;
    private final ResetCodeRepository codeRepository;
    private final TokenHashService tokenHashService;

    public VerifyResetCodeUseCase(
            UserRepository userRepository,
            ResetCodeRepository codeRepository,
            TokenHashService tokenHashService
    ) {
        this.userRepository = userRepository;
        this.codeRepository = codeRepository;
        this.tokenHashService = tokenHashService;
    }

    @Override
    @Transactional
    public Void execute(VerifyResetCodeInput input) {
        System.out.println("\n\nEntrou no caso de uso---------------------------------------\n\n");

        User user = userRepository.findByEmail(input.email())
                .orElseThrow(InvalidTokenException::new);

        System.out.println("\nAchou o Prayer\n");

        PasswordResetCode resetCode =
                codeRepository.findLatestByUserId(user.getUserId())
                        .orElseThrow(InvalidTokenException::new);

        System.out.println("\nAchou o Código\n");

        resetCode.validate(input.code(), tokenHashService);

        codeRepository.save(resetCode);

        return null;
    }
}
