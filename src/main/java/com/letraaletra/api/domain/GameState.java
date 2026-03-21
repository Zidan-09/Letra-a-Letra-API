package com.letraaletra.api.domain;

import com.letraaletra.api.domain.board.Board;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.player.Player;
import com.letraaletra.api.domain.player.Spectator;

public class GameState {
    String roomId;
    String roomName;
    GameStatus status;
    Player[] players;
    Spectator[] spectators;
    int turn;
    Board board;
}
