package com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.stats.UserStats;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserStatsJpaEntity;

public class UserStatsMapper {
    public static UserStats toDomain(UserStatsJpaEntity entity) {
        return UserStats.restore(
                entity.getTotalMatches(),
                entity.getTotalWins(),
                entity.getWinStreak(),
                entity.getLevel(),
                entity.getExperience(),
                entity.getRankingPoints()
        );
    }

    public static UserStatsJpaEntity toEntity(User user) {
        UserStatsJpaEntity entity = new UserStatsJpaEntity();

        UserStats domain = user.getStats();

        entity.setUserId(user.getUserId());
        entity.setTotalMatches(domain.getTotalMatches());
        entity.setTotalWins(domain.getTotalWins());
        entity.setWinStreak(domain.getWinStreak());
        entity.setLevel(domain.getLevel());
        entity.setExperience(domain.getExperience());
        entity.setRankingPoints(domain.getRankingPoints());

        return entity;
    }
}
