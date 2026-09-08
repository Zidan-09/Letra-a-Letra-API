package com.letraaletra.api.features.reward.infrastructure.service;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.exceptions.InvalidCosmeticException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.features.reward.domain.RewardType;
import com.letraaletra.api.features.reward.application.port.RewardFactory;
import com.letraaletra.api.features.reward.domain.CosmeticReward;
import com.letraaletra.api.features.reward.domain.HardGemsReward;
import com.letraaletra.api.features.reward.domain.Reward;
import com.letraaletra.api.features.reward.domain.SoftCoinsReward;
import com.letraaletra.api.features.reward.domain.exception.InvalidRewardQuantityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RewardFactoryService implements RewardFactory {
    private final CosmeticRepository cosmeticRepository;

    public Reward create(RewardType type, Integer quantity, UUID referenceId) {
        return switch (type) {
            case COIN -> new SoftCoinsReward(requirePositiveQuantity(quantity));
            case GEMS -> new HardGemsReward(requirePositiveQuantity(quantity));
            case COSMETIC -> {
                if (referenceId == null) {
                    throw new InvalidCosmeticException();
                }
                Cosmetic cosmetic = cosmeticRepository.find(referenceId)
                        .orElseThrow(CosmeticNotFoundException::new);

                if (!cosmetic.isAvailable()) {
                    throw new InvalidCosmeticException();
                }

                yield new CosmeticReward(cosmetic);
            }
        };
    }

    private int requirePositiveQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new InvalidRewardQuantityException();
        }

        return quantity;
    }
}
