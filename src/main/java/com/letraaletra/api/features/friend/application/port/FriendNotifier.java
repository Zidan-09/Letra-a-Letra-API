package com.letraaletra.api.features.friend.application.port;

import java.util.UUID;

public interface FriendNotifier {
    void notifierUser(UUID userId);

    default void notifyFriendshipAccepted(UUID userId) {
        notifierUser(userId);
    }

    default void notifyFriendshipDeclined(UUID userId) {
        notifierUser(userId);
    }

    default void notifyFriendshipRemoved(UUID userId) {
        notifierUser(userId);
    }

    default void notifyFriendshipCancelled(UUID userId) {
        notifierUser(userId);
    }
}
