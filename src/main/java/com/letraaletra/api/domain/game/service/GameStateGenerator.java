package com.letraaletra.api.domain.game.service;

import com.letraaletra.api.domain.game.GameState;
import com.letraaletra.api.domain.game.board.Board;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.game.participant.ParticipantRole;
import com.letraaletra.api.domain.game.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameStateGenerator {
    public GameState generate(List<Participant> participants, Board board) {
        Map<String, Player> players = new HashMap<>();

        participants
                .stream().filter(p -> p.getRole().equals(ParticipantRole.PLAYER))
                .forEach(p -> {
                    Player player = new Player(
                            p.getUserId()
                    );

                    players.put(p.getUserId(), player);
                });

        return new GameState(
                players,
                board
        );
    }
}
