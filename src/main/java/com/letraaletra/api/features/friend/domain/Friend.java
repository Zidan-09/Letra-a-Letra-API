package com.letraaletra.api.features.friend.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Friend {
    private final UUID userId1;
    private final UUID userId2;
    private FriendStatus status;
    private final LocalDateTime requestDate;

    public Friend(
            UUID userId1,
            UUID userId2,
            FriendStatus status,
            LocalDateTime requestDate
    ) {
        this.userId1 = userId1;
        this.userId2 = userId2;
        this.status = status;
        this.requestDate = requestDate;
    }

    public static Friend create(
            UUID userId1,
            UUID userId2
    ) {
        return new Friend(
                userId1,
                userId2,
                FriendStatus.PENDING,
                LocalDateTime.now()
        );
    }

    public static Friend restore(
            UUID userId1,
            UUID userId2,
            FriendStatus status,
            LocalDateTime requestDate
    ) {
        return new Friend(
                userId1,
                userId2,
                status,
                requestDate
        );
    }

    public UUID getUserId1() {
        return userId1;
    }

    public UUID getUserId2() {
        return userId2;
    }

    public FriendStatus getStatus() {
        return status;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setStatus(FriendStatus status) {
        this.status = status;
    }
}
