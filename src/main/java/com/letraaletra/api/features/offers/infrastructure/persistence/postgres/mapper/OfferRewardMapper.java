package com.letraaletra.api.features.offers.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.offers.domain.OfferReward;
import com.letraaletra.api.features.reward.domain.RewardType;
import com.letraaletra.api.features.reward.domain.CosmeticReward;
import com.letraaletra.api.features.reward.domain.HardGemsReward;
import com.letraaletra.api.features.reward.domain.Reward;
import com.letraaletra.api.features.reward.domain.SoftCoinsReward;
import com.letraaletra.api.features.offers.infrastructure.persistence.postgres.entity.OfferRewardJpaEntity;

import java.util.UUID;

public class OfferRewardMapper {
    public static OfferReward toDomain(OfferRewardJpaEntity entity, Reward reward) {
        return new OfferReward(
            entity.getOfferRewardId(),
            reward
        );
    }

    public static OfferRewardJpaEntity toEntity(UUID offerId, OfferReward domain) {
        OfferRewardJpaEntity entity = new OfferRewardJpaEntity();

        entity.setOfferRewardId(domain.offerRewardId());
        entity.setOfferId(offerId);

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
