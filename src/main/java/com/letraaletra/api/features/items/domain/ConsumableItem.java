package com.letraaletra.api.features.items.domain;

import com.letraaletra.api.features.items.domain.exception.InvalidConsumableItemDurationException;

import java.util.UUID;

public class ConsumableItem extends Item {
    private EffectType type;
    private int duration;

    private ConsumableItem(
            UUID itemId,
            String name,
            int version,
            boolean available,
            EffectType type,
            int duration
    ) {
        super(itemId, name, version, available);
        this.type = type;
        this.duration = duration;
    }

    public static ConsumableItem create(
            String name,
            EffectType effectType,
            int duration
    ) {
        return new ConsumableItem(
                UUID.randomUUID(),
                name,
                1,
                true,
                effectType,
                duration
        );
    }

    public EffectType getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }

    public void setType(EffectType type) {
        this.type = type;
    }

    public void setDuration(int duration) {
        if (duration < 0) {
            throw new InvalidConsumableItemDurationException();
        }

        this.duration = duration;
    }
}
