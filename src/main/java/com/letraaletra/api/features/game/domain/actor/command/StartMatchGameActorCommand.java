package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.service.TurnTimeoutManager;

public class StartMatchGameActorCommand implements ActorCommand<Game> {
    private final Board board;
    private final TurnTimeoutManager turnTimeoutManager;

    public StartMatchGameActorCommand(Board board, TurnTimeoutManager turnTimeoutManager) {
        this.board = board;
        this.turnTimeoutManager = turnTimeoutManager;
    }

    @Override
    public Game execute(Game game) {
        game.start(board);

        turnTimeoutManager.start(game);

        return game;
    }
}
