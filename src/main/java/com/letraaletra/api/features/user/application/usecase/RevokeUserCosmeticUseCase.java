package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.user.application.input.RevokeUserCosmeticInput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class RevokeUserCosmeticUseCase implements UseCase<RevokeUserCosmeticInput, Void> {
    private final UserRepository userRepository;
    private final AdminChecker adminChecker;

    public RevokeUserCosmeticUseCase(
            UserRepository userRepository,
            AdminChecker adminChecker
    ) {
        this.userRepository = userRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public Void execute(RevokeUserCosmeticInput input) {
        adminChecker.check(input.principal(), PermissionKey.USER, PermissionAction.EDIT);

        User user = userRepository.find(input.userId())
                .orElseThrow(UserNotFoundException::new);

        user.getInventory().removeFromInventory(input.cosmeticId());

        userRepository.save(user);

        return null;
    }
}
