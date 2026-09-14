package com.letraaletra.api.features.reward.infrastructure.service;

import com.letraaletra.api.features.inventory.domain.EffectType;
import com.letraaletra.api.features.inventory.domain.ItemCategory;
import com.letraaletra.api.features.inventory.domain.ItemContext;
import com.letraaletra.api.features.inventory.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.ItemEffect;
import com.letraaletra.api.features.inventory.domain.ItemKind;
import com.letraaletra.api.features.inventory.domain.exception.ItemNotAvailableException;
import com.letraaletra.api.features.inventory.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.inventory.domain.repository.ItemDefinitionRepository;
import com.letraaletra.api.features.reward.domain.ItemGrantReward;
import com.letraaletra.api.features.reward.domain.Reward;
import com.letraaletra.api.features.reward.domain.RewardType;
import com.letraaletra.api.features.reward.domain.exception.InvalidRewardQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RewardFactoryService Unit Tests")
class RewardFactoryServiceTest {

    @Mock
    private ItemDefinitionRepository itemDefinitionRepository;

    @InjectMocks
    private RewardFactoryService factory;

    private ItemDefinition avatar;
    private ItemDefinition boost;

    @BeforeEach
    void setUp() {
        avatar = ItemDefinition.create(
                "Blue Avatar",
                ItemKind.COSMETIC,
                ItemCategory.AVATAR,
                Set.of(ItemContext.PROFILE),
                false,
                null,
                false,
                null,
                "/assets/avatar/blue.png"
        );
        boost = ItemDefinition.create(
                "XP Boost 50%",
                ItemKind.CONSUMABLE,
                ItemCategory.XP_BOOST,
                Set.of(ItemContext.PROFILE),
                true,
                10,
                true,
                new ItemEffect(EffectType.XP_BOOST_PCT, 50, 60),
                null
        );
    }

    @Test
    @DisplayName("ITEM stackável deve gerar ItemGrantReward com a quantidade")
    void stackableItemShouldGenerateItemGrantReward() {
        when(itemDefinitionRepository.findById(boost.getId())).thenReturn(Optional.of(boost));

        Reward reward = factory.create(RewardType.ITEM, 3, boost.getId());

        assertEquals(new ItemGrantReward(boost.getId(), 3), reward);
    }

    @Test
    @DisplayName("ITEM único deve exigir quantidade 1")
    void uniqueItemShouldRequireQuantityOne() {
        when(itemDefinitionRepository.findById(avatar.getId())).thenReturn(Optional.of(avatar));

        assertEquals(new ItemGrantReward(avatar.getId(), 1),
                factory.create(RewardType.ITEM, 1, avatar.getId()));
        assertThrows(InvalidRewardQuantityException.class,
                () -> factory.create(RewardType.ITEM, 2, avatar.getId()));
    }

    @Test
    @DisplayName("ITEM sem referência ou inexistente deve falhar")
    void itemWithoutReferenceShouldFail() {
        assertThrows(ItemNotFoundException.class,
                () -> factory.create(RewardType.ITEM, 1, null));

        UUID missing = UUID.randomUUID();
        when(itemDefinitionRepository.findById(missing)).thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class,
                () -> factory.create(RewardType.ITEM, 1, missing));
    }

    @Test
    @DisplayName("ITEM indisponível deve falhar")
    void unavailableItemShouldFail() {
        boost.setAvailable(false);
        when(itemDefinitionRepository.findById(boost.getId())).thenReturn(Optional.of(boost));

        assertThrows(ItemNotAvailableException.class,
                () -> factory.create(RewardType.ITEM, 1, boost.getId()));
    }
}
