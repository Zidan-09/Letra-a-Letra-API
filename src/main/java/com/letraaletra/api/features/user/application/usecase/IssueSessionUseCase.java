package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.IssueSessionInput;
import com.letraaletra.api.features.user.application.output.IssueSessionOutput;
import com.letraaletra.api.features.user.application.port.RefreshTokenGenerator;
import com.letraaletra.api.features.user.domain.session.UserSession;
import com.letraaletra.api.features.user.domain.session.repository.UserSessionRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.service.TokenHashService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class IssueSessionUseCase implements UseCase<IssueSessionInput, IssueSessionOutput> {
    private final UserSessionRepository sessionRepository;
    private final RefreshTokenGenerator refreshTokenGenerator;
    private final TokenHashService tokenHashService;
    private final long refreshExpirationMillis;

    public IssueSessionUseCase(
            UserSessionRepository sessionRepository,
            RefreshTokenGenerator refreshTokenGenerator,
            TokenHashService tokenHashService,
            long refreshExpirationMillis
    ) {
        this.sessionRepository = sessionRepository;
        this.refreshTokenGenerator = refreshTokenGenerator;
        this.tokenHashService = tokenHashService;
        this.refreshExpirationMillis = refreshExpirationMillis;
    }

    @Override
    public IssueSessionOutput execute(IssueSessionInput input) {
        LocalDateTime now = LocalDateTime.now();

        String refreshToken = refreshTokenGenerator.generate();
        String tokenHash = tokenHashService.hash(refreshToken);
        LocalDateTime expiresAt = now.plus(refreshExpirationMillis, ChronoUnit.MILLIS);

        Optional<UserSession> existing = sessionRepository.findByUserIdForUpdate(input.userId());

        if (existing.isPresent()) {
            UserSession session = existing.get();
            session.renew(tokenHash, now, expiresAt);
            sessionRepository.save(session);
        } else {
            UserSession session = UserSession.create(input.userId(), tokenHash, now, expiresAt);
            sessionRepository.save(session);
        }

        return new IssueSessionOutput(refreshToken);
    }
}
