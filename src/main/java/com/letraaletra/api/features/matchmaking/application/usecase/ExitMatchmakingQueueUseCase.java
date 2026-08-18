package com.letraaletra.api.features.matchmaking.application.usecase;

import com.letraaletra.api.features.matchmaking.application.input.ExitMatchmakingQueueInput;
import com.letraaletra.api.features.matchmaking.domain.repository.MatchmakingRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserAlreadyInGameException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.QueueChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.exception.UserIsNotOnQueueException;

public class ExitMatchmakingQueueUseCase implements UseCase<ExitMatchmakingQueueInput, Void> {
    private final MatchmakingRepository matchmakingRepository;
    private final UserRepository userRepository;
    private final QueueChecker queueChecker;

    public ExitMatchmakingQueueUseCase(
            MatchmakingRepository matchmakingRepository,
            UserRepository userRepository,
            QueueChecker queueChecker
    ) {
        this.matchmakingRepository = matchmakingRepository;
        this.userRepository = userRepository;
        this.queueChecker = queueChecker;
    }

    @Override
    public Void execute(ExitMatchmakingQueueInput input) {
        boolean isOnQueue = queueChecker.checkQueues(input.userId());

        if (!isOnQueue) throw new UserIsNotOnQueueException();

        boolean isOnRanking = matchmakingRepository.onQueue(input.userId());

        if (!isOnRanking) throw new UserIsNotOnQueueException();

        User user = userRepository.find(input.userId())
                .orElseThrow(UserIsNotOnQueueException::new);

        if (!user.isNotInGame()) throw new UserAlreadyInGameException();

        matchmakingRepository.remove(input.userId());

        return null;
    }
}
