package com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board;

import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board.cell.EffectView;

public record BoardDTO(
        boolean revealed,
        Character letter,
        String revealedBy,
        EffectView effect
) {
}
