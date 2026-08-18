package com.letraaletra.api.features.ranking.application.usecase;

import com.letraaletra.api.features.ranking.application.input.ExitRankingQueueInput;
import com.letraaletra.api.features.ranking.domain.repository.RankingRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserAlreadyInGameException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.QueueChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.exception.UserIsNotOnQueueException;

public class ExitRankingQueueUseCase implements UseCase<ExitRankingQueueInput, Void> {
    private final RankingRepository rankingRepository;
    private final UserRepository userRepository;
    private final QueueChecker queueChecker;

    public ExitRankingQueueUseCase(
            RankingRepository rankingRepository,
            UserRepository userRepository,
            QueueChecker queueChecker
    ) {
        this.rankingRepository = rankingRepository;
        this.userRepository = userRepository;
        this.queueChecker = queueChecker;
    }

    @Override
    public Void execute(ExitRankingQueueInput input) {
        boolean isOnQueue = queueChecker.checkQueues(input.userId());

        if (!isOnQueue) throw new UserIsNotOnQueueException();

        boolean isOnRanking = rankingRepository.onQueue(input.userId());

        if (!isOnRanking) throw new UserIsNotOnQueueException();

        User user = userRepository.find(input.userId())
                .orElseThrow(UserIsNotOnQueueException::new);

        if (!user.isNotInGame()) throw new UserAlreadyInGameException();

        rankingRepository.remove(input.userId());

        return null;
    }
}
