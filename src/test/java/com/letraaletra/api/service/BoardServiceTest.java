package com.letraaletra.api.service;

import com.letraaletra.api.domain.board.Board;
import com.letraaletra.api.domain.board.Cell;
import com.letraaletra.api.domain.game.GameMode;
import com.letraaletra.api.domain.theme.Theme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardServiceTest {

    @InjectMocks
    private BoardService boardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return a Cell matrix with range of 10x10")
    void createBoard() {
        List<String> words = new ArrayList<>();

        words.add("python");
        words.add("typescript");
        words.add("java");
        words.add("flutter");
        words.add("spring");
        words.add("database");
        words.add("software");
        words.add("hardware");
        words.add("pipeline");
        words.add("migration");

        Theme theme = new Theme("tech", words);

        Board board = boardService.createBoard(theme, GameMode.NORMAL);

        assertNotNull(board);
        assertEquals(10, board.getGrid().length);
        assertEquals(10, board.getGrid()[0].length);

        for (Cell[] row : board.getGrid()) {
            for (Cell cell : row) {
                System.out.print(cell.getLetter() + " ");
            }
            System.out.println();
        }
    }
}