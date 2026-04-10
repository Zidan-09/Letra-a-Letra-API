package com.letraaletra.api.domain.game.board;

import com.letraaletra.api.domain.game.board.cell.Cell;
import com.letraaletra.api.domain.game.board.cell.PowerType;
import com.letraaletra.api.domain.game.board.cell.effect.TrapEffect;
import com.letraaletra.api.domain.game.board.position.Position;
import com.letraaletra.api.domain.game.board.word.Word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record Board(Cell[][] grid, Word[] words) {

    public Cell getCell(Position position) {
        return grid[position.x()][position.y()];
    }

    @Override
    public Word[] words() {
        return words.clone();
    }

    public List<Position> getOpponentTraps(String user) {
        return Arrays.stream(grid)
                .flatMap(Arrays::stream)
                .filter(cell -> cell.getEffect() instanceof TrapEffect)
                .filter(cell -> !Objects.equals(cell.getEffect().getOwnerId(), user))
                .map(Cell::getPosition)
                .toList();
    }
}
