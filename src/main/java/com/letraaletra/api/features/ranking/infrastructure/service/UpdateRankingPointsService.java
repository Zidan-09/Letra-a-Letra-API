package com.letraaletra.api.features.ranking.infrastructure.service;

import com.letraaletra.api.features.ranking.application.port.RankingPointsService;
import com.letraaletra.api.features.ranking.domain.UpdateRankingPoints;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.effect.effects.RankProtectionEffect;
import com.letraaletra.api.features.user.domain.effect.effects.RankingPointsBonusEffect;
import com.letraaletra.api.features.user.domain.stats.UserStats;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class UpdateRankingPointsService implements RankingPointsService {

    public UpdateRankingPoints handle(User user, int userPoints, int opponentPoints) {
        int pointsBefore = user.getStats().getRankingPoints();

        Optional<RankProtectionEffect> shield = user.getActiveEffects().find(RankProtectionEffect.class)
                .filter(effect -> effect.getRemainingUses() > 0);

        if (UserStats.calculatePointsDelta(userPoints, opponentPoints) < 0 && shield.isPresent()) {
            shield.get().use();
            user.getActiveEffects().updateEffects();

            return new UpdateRankingPoints(pointsBefore, 0, pointsBefore);
        }

        int changed = user.getStats()
                .incrementPoints(
                        userPoints,
                        opponentPoints
                );

        changed = applyRankingBonus(user, changed);

        int pointsAfter = user.getStats().getRankingPoints();
        user.getActiveEffects().updateEffects();

        return new UpdateRankingPoints(pointsBefore, changed, pointsAfter);
    }

    private int applyRankingBonus(User user, int changed) {
        if (changed <= 0) {
            return changed;
        }

        return user.getActiveEffects().find(RankingPointsBonusEffect.class)
                .filter(effect -> effect.isValid(Instant.now()))
                .map(effect -> changed + changed * effect.getBonusPercentage() / 100)
                .orElse(changed);
    }
}
