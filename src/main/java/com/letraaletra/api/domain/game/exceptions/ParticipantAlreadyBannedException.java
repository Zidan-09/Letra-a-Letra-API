package com.letraaletra.api.domain.game.exceptions;

import com.letraaletra.api.domain.game.GameMessages;
import com.letraaletra.api.exception.WebSocketException;

public class ParticipantAlreadyBannedException extends WebSocketException {
    public ParticipantAlreadyBannedException() {
        super(GameMessages.PARTICIPANT_ALREADY_BANNED);
    }
}
