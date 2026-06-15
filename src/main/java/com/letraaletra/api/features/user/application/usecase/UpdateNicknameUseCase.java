package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.UpdateNicknameInput;
import com.letraaletra.api.features.user.application.output.UpdateNicknameOutput;
import com.letraaletra.api.features.user.domain.exceptions.UserCannotChangeNicknameException;
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

    public UpdateNicknameOutput execute(UpdateNicknameInput input) {
        User user = userRepository.find(input.user()).orElse(null);
        validateUser(user);
        validateNickname(input.nickname());

        user.setNickname(input.nickname());
        user.setCanChangeNickname(false);

        userRepository.save(user);

        return buildOutput(user.getNickname());
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

    private UpdateNicknameOutput buildOutput(String nickname) {
        return new UpdateNicknameOutput(nickname);
    }
}
