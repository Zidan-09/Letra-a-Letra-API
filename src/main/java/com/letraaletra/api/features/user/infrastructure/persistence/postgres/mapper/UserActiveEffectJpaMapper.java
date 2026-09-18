package com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.user.domain.effect.UserEffect;
import com.letraaletra.api.features.user.domain.effect.effects.ExperienceBonusEffect;
import com.letraaletra.api.features.user.domain.effect.effects.RankProtectionEffect;
import com.letraaletra.api.features.user.domain.exception.InvalidUserEffectException;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserActiveEffectJpaEntity;

import java.util.UUID;

public class UserActiveEffectJpaMapper {
    private static final String XP_BOOST = "XP_BOOST";
    private static final String RANK_PROTECTION = "RANK_PROTECTION";

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

        throw new InvalidUserEffectException();
    }
}
