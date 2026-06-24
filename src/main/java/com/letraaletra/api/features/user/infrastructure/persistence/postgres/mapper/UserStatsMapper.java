package com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.user.domain.stats.UserStats;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserStatsJpaEntity;

import java.util.UUID;

public class UserStatsMapper {
    public static UserStats toDomain(UserStatsJpaEntity entity) {
        return new UserStats(
                entity.getTotalMatches(),
                entity.getTotalWins(),
                entity.getWinStreak(),
                entity.getLevel(),
                entity.getExperience(),
                entity.getRankingPoints()
        );
    }

    public static UserStatsJpaEntity toEntity(UserStats domain, UUID userId) {
        UserStatsJpaEntity entity = new UserStatsJpaEntity();

        entity.setUserId(userId);
        entity.setTotalMatches(domain.getTotalMatches());
        entity.setTotalWins(domain.getTotalWins());
        entity.setWinStreak(domain.getWinStreak());
        entity.setLevel(domain.getLevel());
        entity.setExperience(domain.getExperience());
        entity.setRankingPoints(domain.getPoints());

        return entity;
    }
}
