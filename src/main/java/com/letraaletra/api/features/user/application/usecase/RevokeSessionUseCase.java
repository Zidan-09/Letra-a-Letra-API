package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.RevokeSessionInput;
import com.letraaletra.api.features.user.domain.session.SessionRevocationReason;
import com.letraaletra.api.features.user.domain.session.UserSession;
import com.letraaletra.api.features.user.domain.session.repository.UserSessionRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.time.LocalDateTime;
import java.util.Optional;

public class RevokeSessionUseCase implements UseCase<RevokeSessionInput, Void> {
    private final UserSessionRepository sessionRepository;

    public RevokeSessionUseCase(UserSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public Void execute(RevokeSessionInput input) {
        Optional<UserSession> session = sessionRepository.findByUserId(input.principal().auth());

        if (session.isEmpty()) {
            return null;
        }

        session.get().revoke(SessionRevocationReason.LOGOUT, LocalDateTime.now());
        sessionRepository.save(session.get());

        return null;
    }
}
