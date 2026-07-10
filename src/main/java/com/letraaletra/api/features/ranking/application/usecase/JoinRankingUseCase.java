package com.letraaletra.api.features.ranking.application.usecase;

import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserAlreadyInGameException;
import com.letraaletra.api.shared.application.port.QueueChecker;
import com.letraaletra.api.shared.domain.exception.UserAlreadyOnQueueException;
import com.letraaletra.api.features.ranking.application.input.JoinRankingInput;
import com.letraaletra.api.features.ranking.domain.repository.RankingRepository;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.OnlineUser;

public class JoinRankingUseCase implements UseCase<JoinRankingInput, Void> {
    private final RankingRepository rankingRepository;
    private final UserRepository userRepository;
    private final QueueChecker queueChecker;

    public JoinRankingUseCase(
            RankingRepository rankingRepository,
            UserRepository userRepository,
            QueueChecker queueChecker
    ) {
        this.rankingRepository = rankingRepository;
        this.userRepository = userRepository;
        this.queueChecker = queueChecker;
    }

    @Override
    public Void execute(JoinRankingInput input) {
        OnlineUser onlineUser = input.onlineUser();

        User user = userRepository.find(onlineUser.userId())
                .orElseThrow(UserNotFoundException::new);

        if (!user.isNotInGame()) throw new UserAlreadyInGameException();

        boolean alreadyOnQueue = queueChecker.checkQueues(onlineUser.userId());

        if (alreadyOnQueue) throw new UserAlreadyOnQueueException();

        rankingRepository.add(onlineUser);

        return null;
    }
}
