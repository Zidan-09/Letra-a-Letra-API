package com.letraaletra.api.features.friend.domain;

import java.time.LocalDateTime;

public class Friend {
    private final String userId1;
    private final String userId2;
    private FriendStatus status;
    private final LocalDateTime requestDate;

    public Friend(
            String userId1,
            String userId2,
            FriendStatus status,
            LocalDateTime requestDate
    ) {
        this.userId1 = userId1;
        this.userId2 = userId2;
        this.status = status;
        this.requestDate = requestDate;
    }

    public String getUserId1() {
        return userId1;
    }

    public String getUserId2() {
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
