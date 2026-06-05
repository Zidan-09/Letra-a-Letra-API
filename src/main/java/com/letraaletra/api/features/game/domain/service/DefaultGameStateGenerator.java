package com.letraaletra.api.features.game.domain.service;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.state.GameMode;
import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.board.service.BoardGenerator;

import java.util.List;

public class DefaultGameStateGenerator {
    private final GameStateGenerator gameStateGenerator;
    private final BoardGenerator boardGenerator;

    public DefaultGameStateGenerator(GameStateGenerator gameStateGenerator, BoardGenerator boardGenerator) {
        this.gameStateGenerator = gameStateGenerator;
        this.boardGenerator = boardGenerator;
    }

    public GameState generate(Game game, GameMode gameMode, List<String> words) {
        Board board = boardGenerator.generate(words, gameMode);
        return gameStateGenerator.generate(game.getParticipants(), board);
    }
}
