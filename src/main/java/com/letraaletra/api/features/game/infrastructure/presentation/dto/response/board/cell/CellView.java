package com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board.cell;

public record CellView(
        boolean revealed,
        Character letter,
        String revealedBy,
        EffectView effect
) {
}
