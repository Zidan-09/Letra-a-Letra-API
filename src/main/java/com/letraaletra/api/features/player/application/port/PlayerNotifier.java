package com.letraaletra.api.features.player.application.port;

import java.util.Collection;
import java.util.UUID;

public interface PlayerNotifier {
    void notifyAll(Collection<String> socketIds, Object dto);

    void notifyUser(UUID userId, Object dto);
}
