package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.ForgotPasswordInput;
import com.letraaletra.api.features.user.application.port.ResetCodeService;
import com.letraaletra.api.features.user.application.port.PasswordResetCodeEmailService;
import com.letraaletra.api.features.user.domain.PasswordResetCode;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.repository.reset.ResetCodeRepository;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.shared.domain.service.TokenHashService;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class ForgotPasswordUseCase implements UseCase<ForgotPasswordInput, Void> {
    private final UserRepository userRepository;
    private final TokenHashService tokenHashService;
    private final ResetCodeRepository codeRepository;
    private final ResetCodeService resetCodeService;
    private final PasswordResetCodeEmailService emailService;

    public ForgotPasswordUseCase(
            UserRepository userRepository,
            TokenHashService tokenHashService,
            ResetCodeRepository codeRepository,
            ResetCodeService resetCodeService,
            PasswordResetCodeEmailService emailService
    ) {
        this.userRepository = userRepository;
        this.tokenHashService = tokenHashService;
        this.codeRepository = codeRepository;
        this.resetCodeService = resetCodeService;
        this.emailService = emailService;
    }

    @Override
    public Void execute(ForgotPasswordInput input) {
        User user = userRepository.findByEmail(input.email()).orElse(null);

        if (user == null) return null;

        codeRepository.invalidateAllByUserId(user.getUserId());

        String code = resetCodeService.generate();

        String codeHashed = tokenHashService.hash(code);

        PasswordResetCode resetCode = PasswordResetCode.create(
                user.getUserId(),
                codeHashed
        );

        codeRepository.save(resetCode);

        emailService.send(
                user.getEmail(),
                user.getUsername(),
                code
        );

        return null;
    }
}
