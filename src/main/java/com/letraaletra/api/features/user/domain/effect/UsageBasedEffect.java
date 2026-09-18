package com.letraaletra.api.features.user.domain.effect;

public interface UsageBasedEffect extends UserEffect {
    int getRemainingUses();

    void use();
}
