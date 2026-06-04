package com.letraaletra.api.features.participant.domain.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.domain.game.GameMessages;

public class ParticipantAlreadyBannedException extends DomainException {
    public ParticipantAlreadyBannedException() {
        super(GameMessages.PARTICIPANT_ALREADY_BANNED);
    }
}
