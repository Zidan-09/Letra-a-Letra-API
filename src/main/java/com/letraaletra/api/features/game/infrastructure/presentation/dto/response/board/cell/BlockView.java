package com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board.cell;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("BLOCK")
public record BlockView(
    String ownerId,
    Integer remainingClicks
) implements EffectView {
}
