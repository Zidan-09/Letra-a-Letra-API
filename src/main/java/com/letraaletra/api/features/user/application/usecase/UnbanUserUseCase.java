package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.user.application.input.UnbanUserInput;
import com.letraaletra.api.features.user.domain.ban.BanHistory;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserDoesNotHaveBanException;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.banhistory.BanHistoryRepository;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class UnbanUserUseCase implements UseCase<UnbanUserInput, Void> {
    private final UserRepository userRepository;
    private final BanHistoryRepository banHistoryRepository;
    private final AdminChecker adminChecker;

    public UnbanUserUseCase(
            UserRepository userRepository,
            BanHistoryRepository banHistoryRepository,
            AdminChecker adminChecker
    ) {
        this.userRepository = userRepository;
        this.banHistoryRepository = banHistoryRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public Void execute(UnbanUserInput input) {
        adminChecker.check(input.principal(), PermissionKey.USER, PermissionAction.EDIT);

        User user = userRepository.find(input.userId())
                .orElseThrow(UserNotFoundException::new);

        if (!user.isBanned()) {
            throw new UserDoesNotHaveBanException();
        }

        BanHistory banHistory = banHistoryRepository.findActiveByUserId(input.userId())
                .orElseThrow(UserDoesNotHaveBanException::new);

        banHistory.removeBan(input.principal().auth());

        user.unban();

        banHistoryRepository.save(banHistory);
        userRepository.save(user);

        return null;
    }
}
