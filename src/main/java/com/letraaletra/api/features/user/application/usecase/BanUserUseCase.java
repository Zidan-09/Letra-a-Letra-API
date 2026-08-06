package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.user.application.input.BanUserInput;
import com.letraaletra.api.features.user.domain.BanHistory;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.banhistory.BanHistoryRepository;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class BanUserUseCase implements UseCase<BanUserInput, Void> {
    private final UserRepository userRepository;
    private final BanHistoryRepository banHistoryRepository;
    private final AdminChecker adminChecker;

    public BanUserUseCase(
            UserRepository userRepository,
            BanHistoryRepository banHistoryRepository,
            AdminChecker adminChecker
    ) {
        this.userRepository = userRepository;
        this.banHistoryRepository = banHistoryRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public Void execute(BanUserInput input) {
        adminChecker.check(input.principal(), PermissionKey.USER, PermissionAction.EDIT);

        User user = userRepository.find(input.userId())
                .orElseThrow(UserNotFoundException::new);

        user.ban();

        BanHistory banHistory = BanHistory.create(
                input.userId(),
                input.principal().auth(),
                input.reason(),
                input.type(),
                input.expiresIn()
        );

        banHistoryRepository.save(banHistory);
        userRepository.save(user);

        return null;
    }
}
