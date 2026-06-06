package com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board;

import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board.cell.CellView;

import java.util.List;

public record BoardView(
        List<List<CellView>> grid
) {
}
