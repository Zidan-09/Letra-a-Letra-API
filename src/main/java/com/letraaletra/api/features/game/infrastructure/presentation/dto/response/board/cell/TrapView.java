package com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board.cell;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("TRAP")
public record TrapView(
        String ownerId
) implements EffectView {
}
