package com.letraaletra.api.features.game.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum GameMessages implements MessageCode {
    GAME_NOT_FOUND("the game was not found"),
    GAME_ALREADY_STARTED("the game has already started"),
    GAME_ALREADY_FINISHED("the game has already finished"),
    GAME_NOT_STARTED("the game has not started yet"),
    ONLY_HOST_CAN_START("only the host can start the game"),
    ONLY_HOST_CAN_MODERATE("only the host can perform this moderation action"),
    INVALID_MODERATE_ACTION("the requested moderation action is invalid"),

    GAME_IS_RUNNING("the game is already running"),
    GAME_NOT_RUNNING("the game is not running"),
    INSUFFICIENT_PLAYERS("there are not enough players to perform this action"),
    ROOM_FULL("the room is full"),

    YOU_ARE_BANNED_OF_THIS_ROOM("you are banned from this room"),
    PARTICIPANT_ALREADY_BANNED("the participant is already banned"),
    PARTICIPANT_NOT_BANNED("the participant is not banned"),

    NOT_YOUR_TURN("it is not your turn"),
    INVALID_MOVE("the requested move is invalid"),
    CELL_ALREADY_REVEALED("the selected cell has already been revealed"),
    CELL_ALREADY_HAS_AN_EFFECT("the selected cell already has an effect"),
    INVALID_POSITION("the provided position is invalid"),
    SPECTATOR_CAN_NOT_PLAY("spectators cannot play"),

    WORD_ALREADY_FOUND("the word has already been found"),
    THEME_NOT_FOUND("the theme was not found"),

    MAX_PLAYERS_REACHED("the maximum number of players has been reached");

    private final String message;

    GameMessages(String message) {
        this.message = message;
    }

    @Override
    public String getCode() {
        return name();
    }

    public String getMessage() {
        return message;
    }
}