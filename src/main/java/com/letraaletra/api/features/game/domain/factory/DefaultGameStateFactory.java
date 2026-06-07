package com.letraaletra.api.features.game.domain.factory;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.state.GameMode;
import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.board.service.BoardGenerator;

import java.util.List;

public class DefaultGameStateFactory {
    private final GameStateFactory gameStateFactory;
    private final BoardGenerator boardGenerator;

    public DefaultGameStateFactory(GameStateFactory gameStateFactory, BoardGenerator boardGenerator) {
        this.gameStateFactory = gameStateFactory;
        this.boardGenerator = boardGenerator;
    }

    public GameState generate(Game game, GameMode gameMode, List<String> words) {
        Board board = boardGenerator.generate(words, gameMode);
        return gameStateFactory.generate(game.getParticipants(), board);
    }
}
