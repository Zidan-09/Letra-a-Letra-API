package com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board.cell;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "effect"
)
public sealed interface EffectView permits
        BlockView,
        TrapView
{}