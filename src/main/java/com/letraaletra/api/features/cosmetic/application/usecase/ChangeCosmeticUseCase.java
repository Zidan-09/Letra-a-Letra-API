package com.letraaletra.api.features.cosmetic.application.usecase;

import com.letraaletra.api.features.cosmetic.application.input.ChangeCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.ChangeCosmeticOutput;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.cosmetic.domain.Avatar;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.InvalidUserAvatarSelectedException;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;

import java.util.List;

public class ChangeCosmeticUseCase implements UseCase<ChangeCosmeticInput, ChangeCosmeticOutput> {
    private final UserRepository userRepository;

    public ChangeCosmeticUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ChangeCosmeticOutput execute(ChangeCosmeticInput command) {
        User user = userRepository.find(command.userId()).orElse(null);
        validateUser(user);
        validateAvatar(command.avatar(), user);

        user.setAvatar(command.avatar());

        userRepository.save(user);

        return buildOutput(user.getAvatar());
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private void validateAvatar(String avatar, User user) {
        List<Avatar> userAvatars = userRepository.getAvatars(user.getId());

        if (userAvatars.stream().noneMatch(userAvatar -> userAvatar.avatarId().equals(avatar))) {
            throw new InvalidUserAvatarSelectedException();
        }
    }

    private ChangeCosmeticOutput buildOutput(String avatar) {
        return new ChangeCosmeticOutput(avatar);
    }
}
