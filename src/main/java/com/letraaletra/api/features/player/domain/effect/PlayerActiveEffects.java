package com.letraaletra.api.features.player.domain.effect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerActiveEffects {
    private final List<PlayerEffect> effects;

    private PlayerActiveEffects(List<PlayerEffect> effects) {
        this.effects = Objects.requireNonNull(effects);
    }

    public static PlayerActiveEffects create() {
        return new PlayerActiveEffects(new ArrayList<>());
    }

    public List<PlayerEffect> getEffects() {
        return List.copyOf(effects);
    }

    public boolean isFrozen() {
        return effects.stream().anyMatch(effect -> effect instanceof FreezeEffect);
    }

    public void applyEffect(PlayerEffect effect) {
        effects.add(Objects.requireNonNull(effect));
    }

    public void decrementEffectDuration() {
        effects.forEach(PlayerEffect::onTurnPassed);
        effects.removeIf(PlayerEffect::canRemove);
    }

    public void removeEffect(Class<? extends PlayerEffect> type) {
        effects.removeIf(type::isInstance);
    }
}
