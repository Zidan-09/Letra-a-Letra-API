package com.letraaletra.api.features.matchmaking.application.usecase;

import com.letraaletra.api.features.matchmaking.application.input.JoinMatchmakingInput;
import com.letraaletra.api.features.queue.domain.QueueType;
import com.letraaletra.api.features.queue.domain.repository.QueueRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserAlreadyInGameException;
import com.letraaletra.api.features.queue.domain.OnlineUser;
import com.letraaletra.api.features.queue.domain.exception.UserAlreadyOnQueueException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class JoinMatchmakingQueueUseCase implements UseCase<JoinMatchmakingInput, Void> {
    private final QueueRepository queueRepository;
    private final UserRepository userRepository;

    public JoinMatchmakingQueueUseCase(
            QueueRepository queueRepository,
            UserRepository userRepository
    ) {
        this.queueRepository = queueRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Void execute(JoinMatchmakingInput input) {
        OnlineUser onlineUser = input.onlineUser();

        User user = userRepository.find(onlineUser.userId())
                .orElseThrow(UserNotFoundException::new);

        if (!user.isNotInGame()) throw new UserAlreadyInGameException();

        boolean alreadyOnQueue = queueRepository.onQueue(onlineUser.userId());

        if (alreadyOnQueue) throw new UserAlreadyOnQueueException();

        queueRepository.add(QueueType.CASUAL, onlineUser);

        return null;
    }
}
