package com.letraaletra.api.features.matchmaking.application.usecase;

import com.letraaletra.api.features.matchmaking.application.input.ExitMatchmakingQueueInput;
import com.letraaletra.api.features.queue.domain.repository.QueueRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserAlreadyInGameException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.queue.domain.exception.UserIsNotOnQueueException;

public class ExitMatchmakingQueueUseCase implements UseCase<ExitMatchmakingQueueInput, Void> {
    private final QueueRepository queueRepository;
    private final UserRepository userRepository;

    public ExitMatchmakingQueueUseCase(
            QueueRepository queueRepository,
            UserRepository userRepository
    ) {
        this.queueRepository = queueRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Void execute(ExitMatchmakingQueueInput input) {
        boolean isOnQueue = queueRepository.onQueue(input.userId());

        if (!isOnQueue) throw new UserIsNotOnQueueException();

        User user = userRepository.find(input.userId())
                .orElseThrow(UserIsNotOnQueueException::new);

        if (!user.isNotInGame()) throw new UserAlreadyInGameException();

        queueRepository.remove(input.userId());

        return null;
    }
}
