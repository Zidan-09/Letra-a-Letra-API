package com.letraaletra.api.features.ranking.application.usecase;

import com.letraaletra.api.features.queue.domain.QueueType;
import com.letraaletra.api.features.queue.domain.repository.QueueRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserAlreadyInGameException;
import com.letraaletra.api.features.queue.domain.exception.UserAlreadyOnQueueException;
import com.letraaletra.api.features.ranking.application.input.JoinRankingInput;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.queue.domain.OnlineUser;

public class JoinRankingUseCase implements UseCase<JoinRankingInput, Void> {
    private final QueueRepository queueRepository;
    private final UserRepository userRepository;

    public JoinRankingUseCase(
            QueueRepository queueRepository,
            UserRepository userRepository
    ) {
        this.queueRepository = queueRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Void execute(JoinRankingInput input) {
        OnlineUser onlineUser = input.onlineUser();

        User user = userRepository.find(onlineUser.userId())
                .orElseThrow(UserNotFoundException::new);

        if (!user.isNotInGame()) throw new UserAlreadyInGameException();

        boolean alreadyOnQueue = queueRepository.onQueue(onlineUser.userId());

        if (alreadyOnQueue) throw new UserAlreadyOnQueueException();

        queueRepository.add(QueueType.RANKING, onlineUser);

        return null;
    }
}
