package com.letraaletra.api.features.user.domain.effect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ActiveEffects {
    private final List<UserEffect> effects;

    private ActiveEffects(List<UserEffect> effects) {
        this.effects = Objects.requireNonNull(effects);
    }

    public static ActiveEffects create() {
        return new ActiveEffects(new ArrayList<>());
    }

    public void add(UserEffect effect) {
        effects.add(Objects.requireNonNull(effect));
    }

    public void remove(Class<? extends UserEffect> type) {
        Objects.requireNonNull(type);
        effects.removeIf(type::isInstance);
    }

    public boolean has(Class<? extends UserEffect> type) {
        Objects.requireNonNull(type);
        return effects.stream().anyMatch(type::isInstance);
    }

    public <T extends UserEffect> Optional<T> find(Class<T> type) {
        Objects.requireNonNull(type);
        return effects.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst();
    }

    public boolean isEmpty() {
        return effects.isEmpty();
    }

    public List<UserEffect> getEffects() {
        return List.copyOf(effects);
    }

    public void updateEffects() {
        effects.removeIf(UserEffect::canRemove);
    }
}
