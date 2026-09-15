package com.letraaletra.api.features.reward.infrastructure.service;

import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.exception.ItemNotAvailableException;
import com.letraaletra.api.features.items.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.items.domain.repository.ItemDefinitionRepository;
import com.letraaletra.api.features.reward.domain.ItemGrantReward;
import com.letraaletra.api.features.reward.domain.RewardType;
import com.letraaletra.api.features.reward.application.port.RewardFactory;
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
    private final ItemDefinitionRepository itemDefinitionRepository;

    public Reward create(RewardType type, Integer quantity, UUID referenceId) {
        return switch (type) {
            case COIN -> new SoftCoinsReward(requirePositiveQuantity(quantity));
            case GEMS -> new HardGemsReward(requirePositiveQuantity(quantity));
            case ITEM -> {
                if (referenceId == null) {
                    throw new ItemNotFoundException();
                }
                ItemDefinition definition = itemDefinitionRepository.findById(referenceId)
                        .orElseThrow(ItemNotFoundException::new);

                if (!definition.isAvailable()) {
                    throw new ItemNotAvailableException();
                }

                int amount = requirePositiveQuantity(quantity);

                if (!definition.canStack() && amount != 1) {
                    throw new InvalidRewardQuantityException();
                }

                yield new ItemGrantReward(definition.getId(), amount);
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
