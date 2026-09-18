package com.letraaletra.api.features.items.domain.equippable;

import java.util.EnumSet;
import java.util.Set;

public enum EquippableCategory {
    AVATAR,
    BANNER,
    FRAME,
    EMOTE,
    BOARD,
    CELL;

    public Set<EquippableContext> allowedContexts() {
        return switch (this) {
            case BOARD, CELL, EMOTE -> EnumSet.of(EquippableContext.MATCH);
            default -> EnumSet.of(EquippableContext.PROFILE);
        };
    }
}
