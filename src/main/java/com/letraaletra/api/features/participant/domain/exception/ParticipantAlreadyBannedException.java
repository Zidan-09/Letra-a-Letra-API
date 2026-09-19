package com.letraaletra.api.features.participant.domain.exception;

import com.letraaletra.api.shared.domain.exception.ConflictDomainException;
import com.letraaletra.api.features.game.domain.GameMessages;

public class ParticipantAlreadyBannedException extends ConflictDomainException {
    public ParticipantAlreadyBannedException() {
        super(GameMessages.PARTICIPANT_ALREADY_BANNED);
    }
}
