package com.letraaletra.api.features.friend.application.port;

import java.util.UUID;

public interface FriendNotifier {
    void notifierUser(UUID userId, Object dto);
}
