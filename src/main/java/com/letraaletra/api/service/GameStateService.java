package com.letraaletra.api.service;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.GameState;
import com.letraaletra.api.domain.board.Board;
import com.letraaletra.api.domain.game.GameSettings;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.player.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GameStateService {
    @Autowired
    private BoardService boardService;

    public GameState generateGameState(Game game) {
        GameSettings gameSettings = game.getGameSettings();

        List<Participant> participants = game.getParticipants();

        Map<String, Player> players = new HashMap<>();

        participants.forEach(p -> {
            Player player = new Player(
                    p.getUserId(),
                    p.getNickname(),
                    p.getAvatar()
            );

            players.put(p.getUserId(), player);
        });

        Board board = boardService.createBoard(
                gameSettings.getTheme(),
                gameSettings.getGameMode()
        );

        return new GameState(
            players,
            board
        );
    }
}
