package com.letraaletra.api.domain.board;

import com.letraaletra.api.dto.response.game.CellView;

public class Board {
    private Cell[][] grid;
    private Word[] words;

    public Board(Cell[][] grid, Word[] words) {
        this.grid = grid;
        this.words = words;
    }

    public Cell[][] getGrid() {
        return grid;
    }

    public Word[] getWords() {
        return words.clone();
    }

    public CellView[][] toView() {
        CellView[][] view = new CellView[grid.length][grid[0].length];

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                view[i][j] = mapCellToView(grid[i][j]);
            }
        }

        return view;
    }

    private CellView mapCellToView(Cell cell) {
        if (cell.isRevealed()) {
            return new CellView(
                true,
                cell.getLetter(),
                cell.getRevealedById()
            );
        }

        return new CellView(false, null, null);
    }
}
