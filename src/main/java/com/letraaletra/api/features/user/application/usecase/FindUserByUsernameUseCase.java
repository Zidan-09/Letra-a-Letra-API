package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.FindUserByUsernameInput;
import com.letraaletra.api.features.user.application.output.FindUserByUsernameOutput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class FindUserByUsernameUseCase implements UseCase<FindUserByUsernameInput, FindUserByUsernameOutput> {
    private final UserRepository userRepository;

    public FindUserByUsernameUseCase(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public FindUserByUsernameOutput execute(FindUserByUsernameInput input) {
        User user = userRepository.findByUsername(input.username())
                .orElseThrow(UserNotFoundException::new);

        return new FindUserByUsernameOutput(user);
    }
}
