package com.letraaletra.api.features.items.domain;

import java.util.EnumSet;
import java.util.Set;

public enum ItemCategory {
    AVATAR,
    BANNER,
    FRAME,
    EMOTE,
    BOARD_SKIN,
    CELL_SKIN,
    XP_BOOST,
    RANKING_POINTS_BOOST,
    CHANGE_NICKNAME,
    COIN_BOOST,
    RANKING_POINTS_PROTECTION;

    public boolean isConsumableCategory() {
        return switch (this) {
            case XP_BOOST, RANKING_POINTS_BOOST, CHANGE_NICKNAME, COIN_BOOST, RANKING_POINTS_PROTECTION -> true;
            default -> false;
        };
    }

    public Set<EffectType> allowedEffectTypes() {
        return switch (this) {
            case XP_BOOST -> EnumSet.of(EffectType.XP_BOOST_PCT);
            case RANKING_POINTS_BOOST -> EnumSet.of(EffectType.RANKING_POINTS_BOOST_PCT);
            case COIN_BOOST -> EnumSet.of(EffectType.COIN_BOOST_PCT);
            case RANKING_POINTS_PROTECTION -> EnumSet.of(EffectType.RANKING_POINTS_SHIELD);
            case CHANGE_NICKNAME -> EnumSet.of(EffectType.NICKNAME_CHANGE_GRANT);
            default -> EnumSet.noneOf(EffectType.class);
        };
    }
}
