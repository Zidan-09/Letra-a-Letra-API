package com.letraaletra.api.features.ranking.application.port;

import com.letraaletra.api.features.ranking.domain.UpdateRankingPoints;
import com.letraaletra.api.features.user.domain.User;

public interface RankingPointsService {
    UpdateRankingPoints handle(User user, int userPoints, int opponentPoints);
}
