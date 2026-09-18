package com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.user.domain.effect.UserEffect;
import com.letraaletra.api.features.user.domain.effect.effects.CoinBonusEffect;
import com.letraaletra.api.features.user.domain.effect.effects.ExperienceBonusEffect;
import com.letraaletra.api.features.user.domain.effect.effects.RankProtectionEffect;
import com.letraaletra.api.features.user.domain.effect.effects.RankingPointsBonusEffect;
import com.letraaletra.api.features.user.domain.exception.InvalidUserEffectException;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserActiveEffectJpaEntity;

import java.util.UUID;

public class UserActiveEffectJpaMapper {
    private static final String XP_BOOST = "XP_BOOST";
    private static final String RANK_PROTECTION = "RANK_PROTECTION";
    private static final String RANKING_POINTS_BOOST = "RANKING_POINTS_BOOST";
    private static final String COIN_BOOST = "COIN_BOOST";

    public static UserActiveEffectJpaEntity toEntity(UUID userId, UserEffect effect) {
        UserActiveEffectJpaEntity entity = new UserActiveEffectJpaEntity();
        entity.setId(UUID.randomUUID());
        entity.setUserId(userId);

        if (effect instanceof ExperienceBonusEffect bonus) {
            entity.setEffectType(XP_BOOST);
            entity.setBonusPercentage(bonus.getBonusPercentage());
            entity.setExpiresAt(bonus.getExpiresAt());
        } else if (effect instanceof RankProtectionEffect protection) {
            entity.setEffectType(RANK_PROTECTION);
            entity.setRemainingUses(protection.getRemainingUses());
        } else if (effect instanceof RankingPointsBonusEffect ranking) {
            entity.setEffectType(RANKING_POINTS_BOOST);
            entity.setBonusPercentage(ranking.getBonusPercentage());
            entity.setExpiresAt(ranking.getExpiresAt());
        } else if (effect instanceof CoinBonusEffect coin) {
            entity.setEffectType(COIN_BOOST);
            entity.setBonusPercentage(coin.getBonusPercentage());
            entity.setExpiresAt(coin.getExpiresAt());
        } else {
            throw new InvalidUserEffectException();
        }

        return entity;
    }

    public static UserEffect toDomain(UserActiveEffectJpaEntity entity) {
        if (XP_BOOST.equals(entity.getEffectType())) {
            if (entity.getBonusPercentage() == null || entity.getExpiresAt() == null) {
                throw new InvalidUserEffectException();
            }

            return new ExperienceBonusEffect(entity.getBonusPercentage(), entity.getExpiresAt());
        }

        if (RANK_PROTECTION.equals(entity.getEffectType())) {
            if (entity.getRemainingUses() == null) {
                throw new InvalidUserEffectException();
            }

            return RankProtectionEffect.restore(entity.getRemainingUses());
        }

        if (RANKING_POINTS_BOOST.equals(entity.getEffectType())) {
            if (entity.getBonusPercentage() == null || entity.getExpiresAt() == null) {
                throw new InvalidUserEffectException();
            }

            return new RankingPointsBonusEffect(entity.getBonusPercentage(), entity.getExpiresAt());
        }

        if (COIN_BOOST.equals(entity.getEffectType())) {
            if (entity.getBonusPercentage() == null || entity.getExpiresAt() == null) {
                throw new InvalidUserEffectException();
            }

            return new CoinBonusEffect(entity.getBonusPercentage(), entity.getExpiresAt());
        }

        throw new InvalidUserEffectException();
    }
}
