package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.RefreshSessionInput;
import com.letraaletra.api.features.user.application.output.RefreshSessionOutput;
import com.letraaletra.api.features.user.application.port.RefreshTokenGenerator;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.ban.exception.UserBannedFromGameException;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.session.SessionRevocationReason;
import com.letraaletra.api.features.user.domain.session.UserSession;
import com.letraaletra.api.features.user.domain.session.exception.SessionRevokedException;
import com.letraaletra.api.features.user.domain.session.repository.UserSessionRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.exception.SessionExpiredException;
import com.letraaletra.api.shared.domain.security.TokenService;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidTokenException;
import com.letraaletra.api.shared.domain.service.TokenHashService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class RefreshSessionUseCase implements UseCase<RefreshSessionInput, RefreshSessionOutput> {
    private final UserSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final TokenHashService tokenHashService;
    private final RefreshTokenGenerator refreshTokenGenerator;
    private final TokenService tokenService;
    private final long refreshExpirationMillis;
    private final long recoveryWindowMillis;

    public RefreshSessionUseCase(
            UserSessionRepository sessionRepository,
            UserRepository userRepository,
            TokenHashService tokenHashService,
            RefreshTokenGenerator refreshTokenGenerator,
            TokenService tokenService,
            long refreshExpirationMillis,
            long recoveryWindowMillis
    ) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.tokenHashService = tokenHashService;
        this.refreshTokenGenerator = refreshTokenGenerator;
        this.tokenService = tokenService;
        this.refreshExpirationMillis = refreshExpirationMillis;
        this.recoveryWindowMillis = recoveryWindowMillis;
    }

    @Override
    public RefreshSessionOutput execute(RefreshSessionInput input) {
        LocalDateTime now = LocalDateTime.now();

        String tokenHash = tokenHashService.hash(input.refreshToken());

        UserSession session = sessionRepository.findByTokenHashForUpdate(tokenHash)
                .orElseThrow(InvalidTokenException::new);

        if (session.isRevoked()) {
            throw new SessionRevokedException();
        }

        if (session.isExpired(now)) {
            throw new SessionExpiredException();
        }

        User user;
        try {
            user = userRepository.find(session.getUserId())
                    .orElseThrow(UserNotFoundException::new);
        } catch (UserNotFoundException e) {
            session.revoke(SessionRevocationReason.OTHER, now);
            sessionRepository.save(session);
            throw new InvalidTokenException();
        }

        if (user.isBanned()) {
            session.revoke(SessionRevocationReason.USER_BANNED, now);
            sessionRepository.save(session);
            throw new UserBannedFromGameException();
        }

        if (session.matchesCurrent(tokenHash)) {
            return rotate(session, user, now);
        }

        if (session.matchesPrevious(tokenHash)
                && session.isPreviousWithinRecoveryWindow(now, recoveryWindowMillis)) {
            return rotate(session, user, now);
        }

        session.revoke(SessionRevocationReason.TOKEN_REUSE, now);
        sessionRepository.save(session);
        throw new InvalidTokenException();
    }

    private RefreshSessionOutput rotate(UserSession session, User user, LocalDateTime now) {
        String refreshToken = refreshTokenGenerator.generate();
        String newTokenHash = tokenHashService.hash(refreshToken);
        LocalDateTime newExpiresAt = now.plus(refreshExpirationMillis, ChronoUnit.MILLIS);

        session.rotate(newTokenHash, now, newExpiresAt);
        sessionRepository.save(session);

        String accessToken = tokenService.generateUserToken(user.getUserId(), user.getTokenVersion());

        return new RefreshSessionOutput(user.getUserId(), accessToken, refreshToken);
    }
}
