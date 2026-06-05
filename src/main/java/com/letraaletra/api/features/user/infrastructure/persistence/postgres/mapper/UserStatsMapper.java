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
                entity.getPoints()
        );
    }

    public static UserStatsJpaEntity toEntity(UserStats domain, String userId) {
        UserStatsJpaEntity entity = new UserStatsJpaEntity();

        entity.setUserId(UUID.fromString(userId));
        entity.setTotalMatches(domain.getTotalMatches());
        entity.setTotalWins(domain.getTotalWins());
        entity.setWinStreak(domain.getWinStreak());
        entity.setPoints(domain.getPoints());

        return entity;
    }
}
