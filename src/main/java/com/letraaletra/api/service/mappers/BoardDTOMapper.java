package com.letraaletra.api.service.mappers;

import com.letraaletra.api.domain.board.Board;
import com.letraaletra.api.domain.board.Cell;
import com.letraaletra.api.domain.position.Position;
import com.letraaletra.api.dto.response.game.BoardDTO;
import org.springframework.stereotype.Component;

@Component
public class BoardDTOMapper {
    public BoardDTO[][] toDTO(Board board) {
        int range = board.grid().length;

        BoardDTO[][] dto = new BoardDTO[range][range];

        for (int i = 0; i < range; i++) {
            for (int j = 0; j < range; j++) {
                Cell cell = board.getCellOfGrid(new Position(i, j));

                dto[i][j] = mapCellToView(cell);
            }
        }

        return dto;
    }

    private BoardDTO mapCellToView(Cell cell) {
        if (cell.isRevealed()) {
            return new BoardDTO(
                    true,
                    cell.getLetter(),
                    cell.getRevealedById()
            );
        }

        return new BoardDTO(false, null, null);
    }
}
