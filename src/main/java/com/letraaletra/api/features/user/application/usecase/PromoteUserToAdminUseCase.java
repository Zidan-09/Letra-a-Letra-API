package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.PromoteUserToAdminInput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.exceptions.UserIsNotAdminException;

public class PromoteUserToAdminUseCase implements UseCase<PromoteUserToAdminInput, Void> {
    private final UserRepository userRepository;

    public PromoteUserToAdminUseCase(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public Void execute(PromoteUserToAdminInput input) {
        User user = userRepository.find(input.userId())
                .orElseThrow(UserNotFoundException::new);

        validateUser(user);

        User userToPromote = userRepository.find(input.userToPromoteId())
                        .orElseThrow(UserNotFoundException::new);

        userToPromote.setAdmin(true);

        userRepository.save(userToPromote);

        return null;
    }

    private void validateUser(User user) {
        if (!user.isAdmin()) {
            throw new UserIsNotAdminException();
        }
    }
}
