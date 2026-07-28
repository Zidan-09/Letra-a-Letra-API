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
    private final AdminChecker adminChecker;

    public FindUserByUsernameUseCase(
            UserRepository userRepository,
            AdminChecker adminChecker
    ) {
        this.userRepository = userRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public FindUserByUsernameOutput execute(FindUserByUsernameInput input) {
        adminChecker.check(input.principal());

        User user = userRepository.findByUsername(input.username())
                .orElseThrow(UserNotFoundException::new);

        return new FindUserByUsernameOutput(user);
    }
}
