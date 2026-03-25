package com.letraaletra.api.domain.board;

import com.letraaletra.api.domain.position.Position;

public record Board(Cell[][] grid, Word[] words) {

    public Cell getCellOfGrid(Position position) {
        return grid[position.getX()][position.getY()];
    }

    @Override
    public Word[] words() {
        return words.clone();
    }
}
