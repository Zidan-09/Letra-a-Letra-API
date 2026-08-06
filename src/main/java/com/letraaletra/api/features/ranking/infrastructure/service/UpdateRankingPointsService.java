package com.letraaletra.api.features.ranking.infrastructure.service;

import com.letraaletra.api.features.ranking.application.port.RankingPointsService;
import com.letraaletra.api.features.ranking.domain.UpdateRankingPoints;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateRankingPointsService implements RankingPointsService {
    private final UserRepository userRepository;

    public UpdateRankingPoints handle(User user, int userPoints, int opponentPoints) {
        int userPointsBefore = user.getStats().getRankingPoints();

        int userChanged = user.getStats()
                .incrementPoints(
                        userPoints,
                        opponentPoints
                );

        int userPointsAfter = user.getStats().getRankingPoints();

        userRepository.save(user);

        return new UpdateRankingPoints(userPointsBefore, userChanged, userPointsAfter);
    }
}
