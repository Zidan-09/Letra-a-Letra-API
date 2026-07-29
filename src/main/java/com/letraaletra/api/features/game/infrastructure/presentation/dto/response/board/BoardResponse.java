package com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board;

import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board.cell.EffectView;

public record BoardResponse(
        boolean revealed,
        Character letter,
        String revealedBy,
        EffectView effect
) {
}
