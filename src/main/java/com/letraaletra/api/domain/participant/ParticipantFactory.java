package com.letraaletra.api.domain.participant;

import com.letraaletra.api.domain.user.User;


public class ParticipantFactory {
    public static Participant fromUser(User user, String sessionId, ParticipantRole role) {
        return new Participant(
                user.getId(),
                sessionId,
                user.getNickname(),
                user.getAvatar(),
                role
        );
    }
}
