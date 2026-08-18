package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.GetMyProfileInput;
import com.letraaletra.api.features.user.application.output.GetMyProfileOutput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class GetMyProfileUseCase implements UseCase<GetMyProfileInput, GetMyProfileOutput> {
    private final UserRepository userRepository;

    public GetMyProfileUseCase(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public GetMyProfileOutput execute(GetMyProfileInput input) {
        User user = userRepository.find(input.id())
                .orElseThrow(UserNotFoundException::new);

        return new GetMyProfileOutput(user);
    }
}
