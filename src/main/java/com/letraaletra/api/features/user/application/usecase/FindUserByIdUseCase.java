package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.FindUserInput;
import com.letraaletra.api.features.user.application.output.FindUserOutput;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;

public class FindUserByIdUseCase implements UseCase<FindUserInput, FindUserOutput> {
    private final UserRepository userRepository;

    public FindUserByIdUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public FindUserOutput execute(FindUserInput command) {
        User user = userRepository.find(command.id()).orElse(null);
        validateUser(user);

        return buildReturn(user);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private FindUserOutput buildReturn(User user) {
        return new FindUserOutput(
                user.getId(),
                user.getNickname(),
                user.getAvatar(),
                user.getEmail()
        );
    }
}
