package com.letraaletra.api.features.levels.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.levels.domain.LevelReward;
import com.letraaletra.api.features.levels.infrastructure.persistence.postgres.entity.LevelRewardJpaEntity;
import com.letraaletra.api.features.offers.domain.RewardType;
import com.letraaletra.api.shared.domain.rewards.CosmeticReward;
import com.letraaletra.api.shared.domain.rewards.HardGemsReward;
import com.letraaletra.api.shared.domain.rewards.Reward;
import com.letraaletra.api.shared.domain.rewards.SoftCoinsReward;

import java.util.UUID;

public class LevelRewardMapper {
    public static LevelReward toDomain(LevelRewardJpaEntity entity, Reward reward) {
        return new LevelReward(
                entity.getLevelRewardId(),
                reward
        );
    }

    public static LevelRewardJpaEntity toEntity(UUID levelId, LevelReward domain) {
        LevelRewardJpaEntity entity = new LevelRewardJpaEntity();

        entity.setLevelRewardId(domain.levelRewardId());
        entity.setLevelId(levelId);

        switch (domain.reward()) {
            case CosmeticReward reward -> {
                entity.setRewardType(RewardType.COSMETIC);
                entity.setRewardReference(reward.cosmetic().getId());
                entity.setQuantity(1);
            }

            case SoftCoinsReward reward -> {
                entity.setRewardType(RewardType.COIN);
                entity.setRewardReference(null);
                entity.setQuantity(reward.amount());
            }

            case HardGemsReward reward -> {
                entity.setRewardType(RewardType.GEMS);
                entity.setRewardReference(null);
                entity.setQuantity(reward.amount());
            }
        }

        return entity;
    }
}
