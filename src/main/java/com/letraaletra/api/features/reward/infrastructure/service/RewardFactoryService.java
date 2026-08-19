package com.letraaletra.api.features.reward.infrastructure.service;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.features.reward.domain.RewardType;
import com.letraaletra.api.features.reward.application.port.RewardFactory;
import com.letraaletra.api.features.reward.domain.CosmeticReward;
import com.letraaletra.api.features.reward.domain.HardGemsReward;
import com.letraaletra.api.features.reward.domain.Reward;
import com.letraaletra.api.features.reward.domain.SoftCoinsReward;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RewardFactoryService implements RewardFactory {
    private final CosmeticRepository cosmeticRepository;

    public Reward create(RewardType type, Integer quantity, UUID referenceId) {
        return switch (type) {
            case COIN -> new SoftCoinsReward(
                    Objects.requireNonNull(quantity, "Amount cannot be null for COIN")
            );
            case GEMS -> new HardGemsReward(
                    Objects.requireNonNull(quantity, "Amount cannot be null for GEMS")
            );
            case COSMETIC -> {
                UUID id = Objects.requireNonNull(referenceId, "cosmeticId cannot be null for COSMETIC");
                Cosmetic cosmetic = cosmeticRepository.find(id)
                        .orElseThrow(CosmeticNotFoundException::new);

                yield new CosmeticReward(cosmetic);
            }
        };
    }
}
