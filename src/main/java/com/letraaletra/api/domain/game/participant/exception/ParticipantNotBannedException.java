package com.letraaletra.api.domain.game.participant.exception;

import com.letraaletra.api.domain.game.GameMessages;
import com.letraaletra.api.exception.WebSocketException;

public class ParticipantNotBannedException extends WebSocketException {
    public ParticipantNotBannedException() {
        super(GameMessages.PARTICIPANT_NOT_BANNED);
    }
}
