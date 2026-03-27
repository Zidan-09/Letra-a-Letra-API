package com.letraaletra.api.presentation.messages;

import com.letraaletra.api.exception.MessageCode;

public enum GameMessages implements MessageCode {
    GAME_FOUND("game_found"),
    GAMES_FOUND("games_found"),
    GAME_NOT_FOUND("game_not_found"),
    GAME_ALREADY_STARTED("game_already_started"),
    GAME_ALREADY_FINISHED("game_already_finished"),
    GAME_NOT_STARTED("game_not_started"),
    ONLY_HOST_CAN_START("only_host_can_start"),

    NOT_YOUR_TURN("not_your_turn"),
    INVALID_MOVE("invalid_move"),
    CELL_ALREADY_REVEALED("cell_already_revealed"),
    INVALID_POSITION("invalid_position"),

    WORD_ALREADY_FOUND("word_already_found"),

    MAX_PLAYERS_REACHED("max_players_reached");

    private final String message;

    GameMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}