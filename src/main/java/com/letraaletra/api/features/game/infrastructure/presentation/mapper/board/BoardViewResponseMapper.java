package com.letraaletra.api.features.game.infrastructure.presentation.mapper.board;

import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board.BoardView;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board.cell.CellView;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board.BoardResponse;

import java.util.List;

public class BoardViewResponseMapper {
    public static BoardResponse[][] toResponse(BoardView view) {
        int size = view.grid().size();

        BoardResponse[][] dto = new BoardResponse[size][size];

        for (int i = 0; i < size; i++) {
            List<CellView> row = view.grid().get(i);

            for (int j = 0; j < row.size(); j++) {
                CellView cell = row.get(j);

                dto[i][j] = new BoardResponse(
                        cell.revealed(),
                        cell.letter(),
                        cell.revealedBy(),
                        cell.effect()
                );
            }
        }

        return dto;
    }
}