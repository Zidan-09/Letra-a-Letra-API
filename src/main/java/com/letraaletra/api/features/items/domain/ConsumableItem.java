package com.letraaletra.api.features.items.domain;

import java.util.UUID;

public class ConsumableItem extends Item {
    private final ItemEffect effect;

    private ConsumableItem(
            UUID itemId,
            String name,
            int version,
            boolean available,
            ItemEffect effect
    ) {
        super(itemId, name, version, available);

        this.effect = effect;
    }

    public static ConsumableItem create(
            String name,
            ItemEffect effect
    ) {
        return new ConsumableItem(
                UUID.randomUUID(),
                name,
                1,
                true,
                effect
        );
    }

    public static ConsumableItem restore(
            UUID itemId,
            String name,
            int version,
            boolean available,
            ItemEffect effect
    ) {
        return new ConsumableItem(
                itemId,
                name,
                version,
                available,
                effect
        );
    }

    public ItemEffect getEffect() {
        return effect;
    }
}
