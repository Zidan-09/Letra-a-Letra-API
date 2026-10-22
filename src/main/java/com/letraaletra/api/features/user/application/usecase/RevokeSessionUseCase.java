package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.RevokeSessionInput;
import com.letraaletra.api.features.user.domain.session.SessionRevocationReason;
import com.letraaletra.api.features.user.domain.session.UserSession;
import com.letraaletra.api.features.user.domain.session.repository.UserSessionRepository;
import com.letraaletra.api.shared.application.port.WsSessionCloser;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class RevokeSessionUseCase implements UseCase<RevokeSessionInput, Void> {
    private final UserSessionRepository sessionRepository;
    private final WsSessionCloser wsSessionCloser;

    public RevokeSessionUseCase(UserSessionRepository sessionRepository, WsSessionCloser wsSessionCloser) {
        this.sessionRepository = sessionRepository;
        this.wsSessionCloser = wsSessionCloser;
    }

    @Override
    public Void execute(RevokeSessionInput input) {
        UUID userId = input.principal().auth();

        Optional<UserSession> session = sessionRepository.findByUserId(userId);

        if (session.isPresent()) {
            session.get().revoke(SessionRevocationReason.LOGOUT, LocalDateTime.now());
            sessionRepository.save(session.get());
        }

        wsSessionCloser.closeByUserId(userId);

        return null;
    }
}
