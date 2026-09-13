package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.FindUserByUsernameInput;
import com.letraaletra.api.features.user.application.output.FindUserByUsernameOutput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.UsersPage;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.data.domain.Page;

public class FindUserByUsernameUseCase implements UseCase<FindUserByUsernameInput, FindUserByUsernameOutput> {
    private final UserRepository userRepository;

    public FindUserByUsernameUseCase(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public FindUserByUsernameOutput execute(FindUserByUsernameInput input) {
        Page<User> users = userRepository.search(
                input.username(),
                new UsersPage(input.page(), input.size(), input.sort())
        );

        return new FindUserByUsernameOutput(users);
    }
}
