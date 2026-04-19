package com.letraaletra.api.application.usecase.user;

import com.letraaletra.api.application.command.user.SetNicknameCommand;
import com.letraaletra.api.application.output.user.SetNicknameOutput;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.NicknameAlreadyInUseException;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;

public class SetNicknameUseCase {
    private final UserRepository userRepository;

    public SetNicknameUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public SetNicknameOutput execute(SetNicknameCommand command) {
        User user = userRepository.find(command.user()).orElse(null);
        validateUser(user);
        validateNickname(command.nickname(), user);

        user.setNickname(command.nickname());

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

        if (existsNickname || user.getNickname() != null) {
            throw new NicknameAlreadyInUseException();
        }
    }

    private SetNicknameOutput buildOutput(String nickname) {
        return new SetNicknameOutput(nickname);
    }
}
