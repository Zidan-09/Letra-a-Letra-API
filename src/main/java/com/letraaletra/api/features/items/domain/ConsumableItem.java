package com.letraaletra.api.features.items.domain;

import com.letraaletra.api.features.items.domain.exception.InvalidItemException;

import java.util.UUID;

public class ConsumableItem extends Item {
    private final ItemCategory category;
    private final EquippableContext context;
    private final ItemEffect effect;

    private ConsumableItem(
            UUID itemId,
            String name,
            int version,
            boolean available,
            ItemCategory category,
            EquippableContext context,
            ItemEffect effect
    ) {
        super(itemId, name, version, available);

        if (context != EquippableContext.PROFILE) {
            throw new InvalidItemException();
        }

        if (category == null || !category.isConsumableCategory()) {
            throw new InvalidItemException();
        }

        if (effect == null) {
            throw new InvalidItemException();
        }

        validateEffectCompatibility(category, effect);

        this.category = category;
        this.context = context;
        this.effect = effect;
    }

    public static ConsumableItem create(
            String name,
            ItemCategory category,
            EquippableContext context,
            ItemEffect effect
    ) {
        return new ConsumableItem(
                UUID.randomUUID(),
                name,
                1,
                true,
                category,
                context,
                effect
        );
    }

    public static ConsumableItem restore(
            UUID itemId,
            String name,
            int version,
            boolean available,
            ItemCategory category,
            EquippableContext context,
            ItemEffect effect
    ) {
        return new ConsumableItem(
                itemId,
                name,
                version,
                available,
                category,
                context,
                effect
        );
    }

    static void validateEffectCompatibility(ItemCategory category, ItemEffect effect) {
        if (effect instanceof PercentageTimedEffect timed) {
            if (!category.allowedEffectTypes().contains(timed.type())) {
                throw new InvalidItemException();
            }
        } else if (effect instanceof NicknameChangeEffect) {
            if (category != ItemCategory.CHANGE_NICKNAME) {
                throw new InvalidItemException();
            }
        } else {
            throw new InvalidItemException();
        }
    }

    public ItemCategory getCategory() {
        return category;
    }

    public EquippableContext getContext() {
        return context;
    }

    public ItemEffect getEffect() {
        return effect;
    }
}
