package com.letraaletra.api.features.game.domain.factory;

import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.participant.domain.ParticipantRole;
import com.letraaletra.api.features.player.domain.Player;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GameStateFactory {
    public GameState generate(List<Participant> participants, Board board) {
        Map<UUID, Player> players = new HashMap<>();

        participants
                .stream().filter(p -> p.getRole() == ParticipantRole.PLAYER)
                .forEach(p -> {
                    Player player = new Player(
                            p.getUserId()
                    );

                    players.put(p.getUserId(), player);
                });

        UUID matchId = UUID.randomUUID();

        return new GameState(
                matchId,
                players,
                board,
                Instant.now().plusSeconds(45)
        );
    }
}
