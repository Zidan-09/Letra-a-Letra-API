package com.letraaletra.api.features.ranking.application.service;

import com.letraaletra.api.features.ranking.domain.UpdateRankingPoints;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.repository.UserRepository;

public class UpdateRankingPointsService {
    private final UserRepository userRepository;

    public UpdateRankingPointsService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    public UpdateRankingPoints handle(User user, int userPoints, int opponentPoints) {
        int userPointsBefore = user.getStats().getPoints();

        int userChanged = user.getStats()
                .incrementPoints(
                        userPoints,
                        opponentPoints
                );

        int userPointsAfter = user.getStats().getPoints();

        userRepository.save(user);

        return new UpdateRankingPoints(userPointsBefore, userChanged, userPointsAfter);
    }
}
