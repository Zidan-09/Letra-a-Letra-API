package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;
import com.letraaletra.api.features.user.application.input.BanUserInput;
import com.letraaletra.api.features.user.domain.ban.BanHistory;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.ban.repository.BanHistoryRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.session.SessionRevocationReason;
import com.letraaletra.api.features.user.domain.session.repository.UserSessionRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.time.LocalDateTime;

public class BanUserUseCase implements UseCase<BanUserInput, Void> {
    private final UserRepository userRepository;
    private final BanHistoryRepository banHistoryRepository;
    private final AdminChecker adminChecker;
    private final UserSessionRepository sessionRepository;

    public BanUserUseCase(
            UserRepository userRepository,
            BanHistoryRepository banHistoryRepository,
            AdminChecker adminChecker,
            UserSessionRepository sessionRepository
    ) {
        this.userRepository = userRepository;
        this.banHistoryRepository = banHistoryRepository;
        this.adminChecker = adminChecker;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public Void execute(BanUserInput input) {
        adminChecker.check(input.principal(), PermissionKey.USER, PermissionAction.EDIT);

        User user = userRepository.find(input.userId())
                .orElseThrow(UserNotFoundException::new);

        BanHistory banHistory = BanHistory.create(
                input.userId(),
                input.principal().auth(),
                input.reason(),
                input.type(),
                input.expiresIn()
        );

        user.ban(banHistory.getExpiresAt(), banHistory.getReason());

        sessionRepository.findByUserId(user.getUserId()).ifPresent(session -> {
            session.revoke(SessionRevocationReason.USER_BANNED, LocalDateTime.now());
            sessionRepository.save(session);
        });

        banHistoryRepository.save(banHistory);
        userRepository.save(user);

        return null;
    }
}
