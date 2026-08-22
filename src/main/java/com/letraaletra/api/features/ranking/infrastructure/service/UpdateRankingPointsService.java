package com.letraaletra.api.features.ranking.infrastructure.service;

import com.letraaletra.api.features.ranking.application.port.RankingPointsService;
import com.letraaletra.api.features.ranking.domain.UpdateRankingPoints;
import com.letraaletra.api.features.user.domain.User;
import org.springframework.stereotype.Service;

@Service
public class UpdateRankingPointsService implements RankingPointsService {

    public UpdateRankingPoints handle(User user, int userPoints, int opponentPoints) {
        int pointsBefore = user.getStats().getRankingPoints();

        int changed = user.getStats()
                .incrementPoints(
                        userPoints,
                        opponentPoints
                );

        int pointsAfter = user.getStats().getRankingPoints();

        return new UpdateRankingPoints(pointsBefore, changed, pointsAfter);
    }
}
