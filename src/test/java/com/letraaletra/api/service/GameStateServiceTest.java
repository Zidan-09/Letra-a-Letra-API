package com.letraaletra.api.service;

import com.letraaletra.api.domain.board.Board;
import com.letraaletra.api.domain.game.GameMode;
import com.letraaletra.api.domain.position.Position;
import com.letraaletra.api.domain.theme.Theme;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameStateServiceTest {

    @Mock
    private BoardService boardService;

    @InjectMocks
    private GameStateService gameStateService;

    @Test
    @DisplayName("Should return the correspondent letter of the selected cell")
    void revealCell() {
        Theme theme = new Theme("test", List.of("test1", "test2", "test3", "test4", "test5"));
        Board board = boardService.createBoard(theme, GameMode.NORMAL);

        char letter = board.getCellOfGrid(new Position(0, 0)).getLetter();
    }
}