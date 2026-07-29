package com.letraaletra.api.features.game.infrastructure.presentation.mapper.board;

import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board.cell.BlockView;
import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.board.cell.Cell;
import com.letraaletra.api.features.game.domain.board.cell.effect.BlockEffect;
import com.letraaletra.api.features.game.domain.board.position.Position;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board.BoardResponse;

public class BoardResponseMapper {
    public static BoardResponse[][] toResponse(Board board) {
        int range = board.grid().length;

        BoardResponse[][] dto = new BoardResponse[range][range];

        for (int i = 0; i < range; i++) {
            for (int j = 0; j < range; j++) {
                Cell cell = board.getCell(new Position(i, j));

                dto[i][j] = mapCellToView(cell);
            }
        }

        return dto;
    }

    private static BoardResponse mapCellToView(Cell cell) {
        if (cell.isRevealed()) {
            return new BoardResponse(
                    true,
                    cell.getLetter(),
                    cell.getRevealedById().toString(),
                    null
            );
        }

        return new BoardResponse(
                false,
                null,
                null,
                cell.getEffect() instanceof BlockEffect block ?
                        new BlockView(
                                block.getOwnerId().toString(),
                                block.getRemainingAttempts()
                        ) : null
        );
    }
}
