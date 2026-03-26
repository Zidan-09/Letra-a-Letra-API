package com.letraaletra.api.service;

import com.letraaletra.api.domain.board.Board;
import com.letraaletra.api.domain.board.Cell;
import com.letraaletra.api.domain.board.Word;
import com.letraaletra.api.domain.game.GameMode;
import com.letraaletra.api.domain.position.Position;
import com.letraaletra.api.domain.theme.Theme;
import com.letraaletra.api.infra.repository.ThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class BoardService {
    @Autowired
    private ThemeWordSelector themeWordSelector;

    @Autowired
    private ThemeRepository themeRepository;

    public Board createBoard(String themeId, GameMode gameMode) {
        Theme theme = themeRepository.findById(themeId);

        Cell[][] grid = new Cell[10][10];
        List<Word> placedWords = new ArrayList<>();

        List<String> words;

        if (theme != null) {
            words = new ArrayList<>(theme.getWords().stream().limit(5).toList());
        } else {
            words = new ArrayList<>(themeWordSelector.pickRandomThemeWords(5));
        }

        for (String wordValue : words) {
            boolean placed = false;
            int tries = 0;

            while (!placed && tries < 500) {
                tries++;

                int row = ThreadLocalRandom.current().nextInt(grid.length);
                int column = ThreadLocalRandom.current().nextInt(grid[0].length);

                int[][] dirSet = gameMode.getDirections();
                int[] direction = dirSet[ThreadLocalRandom.current().nextInt(dirSet.length)];
                int dx = direction[0];
                int dy = direction[1];

                if (canPlaceWord(wordValue, row, column, dx, dy, grid)) {
                    Word placedWord = placeWord(wordValue, row, column, dx, dy, grid);
                    placed = true;
                    placedWords.add(placedWord);
                }
            }
        }

        fillEmptySpaces(grid);

        bindCellsToWords(grid, placedWords);

        return new Board(grid, placedWords.toArray(new Word[0]));
    }

    private boolean canPlaceWord(String word, int row, int column, int dx, int dy, Cell[][] grid) {
        for (int i = 0; i < word.length(); i++) {
            int x = row + dx * i;
            int y = column + dy * i;

            if (x < 0 || y < 0 || x >= grid.length || y >= grid[0].length) {
                return false;
            }

            if (grid[x][y] != null && grid[x][y].getLetter() != word.charAt(i)) {
                return false;
            }
        }

        return true;
    }

    private Word placeWord(String word, int row, int column, int dx, int dy, Cell[][] grid) {
        List<Position> positions = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            int x = row + dx * i;
            int y = column + dy * i;

            Position position = new Position(x, y);

            if (grid[x][y] == null) {
                grid[x][y] = new Cell(word.charAt(i), position);
            }

            positions.add(position);
        }

        return new Word(word, positions);
    }

    private void fillEmptySpaces(Cell[][] grid) {
        String letters = "abcdefghijklmnopqrstuvwxyz";

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                if (grid[i][j] == null) {
                    int letterIdx = ThreadLocalRandom.current().nextInt(letters.length());
                    char letter = letters.charAt(letterIdx);

                    Position position = new Position(i, j);

                    grid[i][j] = new Cell(letter, position);
                }
            }
        }
    }

    private void bindCellsToWords(Cell[][] grid, List<Word> words) {
        for (Word word : words) {
            for (Position pos : word.getPositions()) {
                Cell cell = grid[pos.getX()][pos.getY()];
                cell.addRelatedWord(word);
            }
        }
    }
}
