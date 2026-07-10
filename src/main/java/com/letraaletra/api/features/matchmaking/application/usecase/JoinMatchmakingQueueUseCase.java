package com.letraaletra.api.features.matchmaking.application.usecase;

import com.letraaletra.api.features.matchmaking.application.input.JoinMatchmakingInput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserAlreadyInGameException;
import com.letraaletra.api.shared.application.port.QueueChecker;
import com.letraaletra.api.shared.domain.OnlineUser;
import com.letraaletra.api.shared.domain.exception.UserAlreadyOnQueueException;
import com.letraaletra.api.features.matchmaking.domain.repository.MatchmakingRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class JoinMatchmakingQueueUseCase implements UseCase<JoinMatchmakingInput, Void> {
    private final MatchmakingRepository matchmakingRepository;
    private final UserRepository userRepository;
    private final QueueChecker queueChecker;

    public JoinMatchmakingQueueUseCase(
            MatchmakingRepository matchmakingRepository,
            UserRepository userRepository,
            QueueChecker queueChecker
    ) {
        this.matchmakingRepository = matchmakingRepository;
        this.userRepository = userRepository;
        this.queueChecker = queueChecker;
    }

    public Void execute(JoinMatchmakingInput input) {
        OnlineUser onlineUser = input.onlineUser();

        User user = userRepository.find(onlineUser.userId())
                .orElseThrow(UserNotFoundException::new);

        if (!user.isNotInGame()) throw new UserAlreadyInGameException();

        boolean alreadyOnQueue = queueChecker.checkQueues(onlineUser.userId());

        if (alreadyOnQueue) throw new UserAlreadyOnQueueException();

        matchmakingRepository.add(onlineUser, input.gameMode());

        return null;
    }
}
