package com.letraaletra.api.features.participant.application.port;

import java.util.Collection;
import java.util.UUID;

public interface ParticipantNotifier {
    void notifyAll(Collection<String> socketIds, Object dto);

    void notifyUser(UUID userId, Object dto);
}
