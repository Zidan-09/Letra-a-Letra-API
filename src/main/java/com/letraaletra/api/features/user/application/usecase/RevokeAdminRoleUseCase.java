package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.RevokeAdminRoleInput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.exceptions.UserIsNotAdminException;

public class RevokeAdminRoleUseCase implements UseCase<RevokeAdminRoleInput, Void> {
    private final UserRepository userRepository;

    public RevokeAdminRoleUseCase(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public Void execute(RevokeAdminRoleInput input) {
        User user = userRepository.find(input.userId())
                .orElseThrow(UserNotFoundException::new);

        validateUser(user);

        User userToRevoke = userRepository.find(input.userToRevokeId())
                .orElseThrow(UserNotFoundException::new);

        userToRevoke.setAdmin(false);

        userRepository.save(userToRevoke);

        return null;
    }

    private void validateUser(User user) {
        if (!user.isAdmin()) {
            throw new UserIsNotAdminException();
        }
    }
}
