package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.ChangeCosmeticInput;
import com.letraaletra.api.features.user.application.output.ChangeCosmeticOutput;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import org.springframework.transaction.annotation.Transactional;

public class ChangeCosmeticUseCase implements UseCase<ChangeCosmeticInput, ChangeCosmeticOutput> {
    private final UserRepository userRepository;

    public ChangeCosmeticUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ChangeCosmeticOutput execute(ChangeCosmeticInput input) {
        User user = userRepository.find(input.userId())
                .orElseThrow(UserNotFoundException::new);

        user.getInventory().equipCosmetic(input.cosmeticId());

        userRepository.save(user);

        return buildOutput(user);
    }

    private ChangeCosmeticOutput buildOutput(User user) {
        return new ChangeCosmeticOutput(
            user
        );
    }
}