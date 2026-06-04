package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.UpdateNicknameInput;
import com.letraaletra.api.features.user.application.output.UpdateNicknameOutput;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.NicknameAlreadyInUseException;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;

public class UpdateNicknameUseCase implements UseCase<UpdateNicknameInput, UpdateNicknameOutput> {
    private final UserRepository userRepository;

    public UpdateNicknameUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UpdateNicknameOutput execute(UpdateNicknameInput command) {
        User user = userRepository.find(command.user()).orElse(null);
        validateUser(user);
        validateNickname(command.nickname(), user);

        user.setNickname(command.nickname());
        user.setCanChangeNickname(false);

        userRepository.save(user);

        return buildOutput(user.getNickname());
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private void validateNickname(String nickname, User user) {
        boolean existsNickname = userRepository.existsByNickname(nickname);

        if (existsNickname || !user.canChangeNickname()) {
            throw new NicknameAlreadyInUseException();
        }
    }

    private UpdateNicknameOutput buildOutput(String nickname) {
        return new UpdateNicknameOutput(nickname);
    }
}
