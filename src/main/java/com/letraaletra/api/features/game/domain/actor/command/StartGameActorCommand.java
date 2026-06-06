package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.application.port.GameTimeoutManager;
import com.letraaletra.api.features.game.application.port.TurnTimeoutManager;
import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.exception.GameIsRunningException;
import com.letraaletra.api.features.game.domain.exception.InsufficientPlayersException;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.participant.domain.exception.OnlyHostCanStartException;
import com.letraaletra.api.features.game.domain.service.GameStateGenerator;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;

public class StartGameActorCommand implements ActorCommand<Game> {
    private final String session;
    private final Board board;
    private final GameStateGenerator gameStateGenerator;
    private final GameTimeoutManager gameTimeoutManager;
    private final TurnTimeoutManager turnTimeoutManager;

    public StartGameActorCommand(String session, Board board, GameStateGenerator gameStateGenerator, GameTimeoutManager gameTimeoutManager, TurnTimeoutManager turnTimeoutManager) {
        this.session = session;
        this.board = board;
        this.gameStateGenerator = gameStateGenerator;
        this.gameTimeoutManager = gameTimeoutManager;
        this.turnTimeoutManager = turnTimeoutManager;
    }

    @Override
    public Game execute(Game game) {

        Participant participant = game.findBySession(session);
        validateParticipant(participant);
        validateHost(participant, game.getHostId());

        if (game.getGameStatus().equals(GameStatus.RUNNING)) {
            throw new GameIsRunningException();
        }

        if (game.getAmountPlayers() < 2) {
            throw new InsufficientPlayersException();
        }

        gameTimeoutManager.cancel(game);

        game.start(board, gameStateGenerator);

        turnTimeoutManager.start(game);

        return game;
    }

    private void validateParticipant(Participant participant) {
        if (participant == null) {
            throw new UserNotFoundException();
        }
    }

    private void validateHost(Participant participant, String hostId) {
        if (!participant.getUserId().equals(hostId)) {
            throw new OnlyHostCanStartException();
        }
    }
}
