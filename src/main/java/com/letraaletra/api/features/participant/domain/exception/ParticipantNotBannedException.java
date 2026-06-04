package com.letraaletra.api.features.participant.domain.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.domain.game.GameMessages;

public class ParticipantNotBannedException extends DomainException {
    public ParticipantNotBannedException() {
        super(GameMessages.PARTICIPANT_NOT_BANNED);
    }
}
