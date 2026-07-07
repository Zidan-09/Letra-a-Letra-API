package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.ChangeNicknameInput;
import com.letraaletra.api.features.user.application.output.ChangeNicknameOutput;
import com.letraaletra.api.features.user.domain.exception.UserCannotChangeNicknameException;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.NicknameAlreadyInUseException;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;

public class ChangeNicknameUseCase implements UseCase<ChangeNicknameInput, ChangeNicknameOutput> {
    private final UserRepository userRepository;

    public ChangeNicknameUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ChangeNicknameOutput execute(ChangeNicknameInput input) {
        User user = userRepository.find(input.user()).orElse(null);
        validateUser(user);
        validateNickname(input.nickname());

        user.setNickname(input.nickname());
        user.setCanChangeNickname(false);

        userRepository.save(user);

        return buildOutput(user);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }

        if (!user.canChangeNickname()) {
           throw new UserCannotChangeNicknameException();
        }
    }

    private void validateNickname(String nickname) {
        boolean existsNickname = userRepository.existsByNickname(nickname);

        if (existsNickname) {
            throw new NicknameAlreadyInUseException();
        }
    }

    private ChangeNicknameOutput buildOutput(User user) {
        return new ChangeNicknameOutput(user);
    }
}
