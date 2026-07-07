package com.letraaletra.api.features.matchmaking.application.usecase;

import com.letraaletra.api.features.matchmaking.application.input.JoinMatchmakingInput;
import com.letraaletra.api.features.matchmaking.domain.MatchUserData;
import com.letraaletra.api.features.matchmaking.domain.exception.UserAlreadyOnQueueException;
import com.letraaletra.api.features.matchmaking.domain.repository.MatchmakingRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class JoinMatchmakingQueueUseCase implements UseCase<JoinMatchmakingInput, Void> {
    private final MatchmakingRepository matchmakingRepository;
    private final UserRepository userRepository;

    public JoinMatchmakingQueueUseCase(
            MatchmakingRepository matchmakingRepository,
            UserRepository userRepository
    ) {
        this.matchmakingRepository = matchmakingRepository;
        this.userRepository = userRepository;
    }

    public Void execute(JoinMatchmakingInput input) {
        MatchUserData matchUserData = input.matchUserData();

        userRepository.find(matchUserData.userId())
                .orElseThrow(UserNotFoundException::new);

        boolean alreadyOnQueue = matchmakingRepository.onQueue(input.matchUserData().userId());

        if (alreadyOnQueue) throw new UserAlreadyOnQueueException();

        matchmakingRepository.add(matchUserData, input.gameMode());

        return null;
    }
}
