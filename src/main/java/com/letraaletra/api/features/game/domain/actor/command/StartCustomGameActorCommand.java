package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.service.GameTimeoutManager;
import com.letraaletra.api.features.game.domain.service.TurnTimeoutManager;
import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.participant.domain.exception.OnlyHostCanStartException;

import java.util.UUID;

public class StartCustomGameActorCommand implements ActorCommand<Game> {
    private final String session;
    private final Board board;
    private final GameTimeoutManager gameTimeoutManager;
    private final TurnTimeoutManager turnTimeoutManager;

    public StartCustomGameActorCommand(String session, Board board, GameTimeoutManager gameTimeoutManager, TurnTimeoutManager turnTimeoutManager) {
        this.session = session;
        this.board = board;
        this.gameTimeoutManager = gameTimeoutManager;
        this.turnTimeoutManager = turnTimeoutManager;
    }

    @Override
    public Game execute(Game game) {

        Participant participant = game.getParticipants().findBySession(session);
        validateHost(participant, game.getHostId());

        gameTimeoutManager.cancel(game);

        game.start(board);

        turnTimeoutManager.start(game);

        return game;
    }

    private void validateHost(Participant participant, UUID hostId) {
        if (!participant.getUserId().equals(hostId)) {
            throw new OnlyHostCanStartException();
        }
    }
}
