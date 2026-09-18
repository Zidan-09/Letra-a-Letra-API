package com.letraaletra.api.features.items.infrastructure.presentation.mapper;

import com.letraaletra.api.features.items.domain.consumable.ConsumableItem;
import com.letraaletra.api.features.items.domain.equippable.EquippableItem;
import com.letraaletra.api.features.items.domain.item.Item;
import com.letraaletra.api.features.items.domain.equippable.EquippableCategory;
import com.letraaletra.api.features.items.domain.equippable.EquippableContext;
import com.letraaletra.api.features.items.domain.item.ItemKind;
import com.letraaletra.api.features.items.domain.effect.NicknameChangeEffect;
import com.letraaletra.api.features.items.domain.effect.PercentageTimedEffect;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemResponse;

public class ItemResponseMapper {
    public static ItemResponse toResponse(Item item) {
        boolean consumable = item instanceof ConsumableItem;

        return new ItemResponse(
                item.getId(),
                item.getName(),
                consumable ? ItemKind.CONSUMABLE : ItemKind.EQUIPPABLE,
                categoryOf(item),
                contextOf(item),
                consumable,
                consumable ? 1000 : null,
                consumable,
                toEffectResponse(item),
                item instanceof EquippableItem equippable ? equippable.getAssetPath() : null,
                item.getVersion(),
                item.isAvailable()
        );
    }

    private static EquippableCategory categoryOf(Item item) {
        if (item instanceof EquippableItem equippable) {
            return equippable.getCategory();
        }

        return null;
    }

    private static EquippableContext contextOf(Item item) {
        if (item instanceof EquippableItem equippable) {
            return equippable.getContext();
        }

        return null;
    }

    private static ItemResponse.ItemEffectResponse toEffectResponse(Item item) {
        if (!(item instanceof ConsumableItem consumable) || consumable.getEffect() == null) {
            return null;
        }

        if (consumable.getEffect() instanceof NicknameChangeEffect) {
            return new ItemResponse.NicknameChangeEffectResponse();
        }

        if (consumable.getEffect() instanceof PercentageTimedEffect timed) {
            return new ItemResponse.PercentageTimedEffectResponse(
                    timed.type(),
                    timed.magnitude(),
                    timed.durationMinutes()
            );
        }

        return null;
    }
}
