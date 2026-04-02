package com.letraaletra.api.domain.game.board;

import com.letraaletra.api.domain.game.board.cell.Cell;
import com.letraaletra.api.domain.game.board.position.Position;
import com.letraaletra.api.domain.game.board.word.Word;

public record Board(Cell[][] grid, Word[] words) {

    public Cell getCell(Position position) {
        return grid[position.x()][position.y()];
    }

    @Override
    public Word[] words() {
        return words.clone();
    }
}
