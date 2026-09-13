package com.letraaletra.api.features.friend.domain;

import com.letraaletra.api.features.friend.domain.exception.CanNotDeclineTheRequestException;
import com.letraaletra.api.features.friend.domain.exception.FriendNotFoundException;
import com.letraaletra.api.features.friend.domain.exception.FriendRequestStillPendingException;
import com.letraaletra.api.features.friend.domain.exception.InvalidFriendRequestException;
import com.letraaletra.api.features.friend.domain.exception.CanNotAcceptTheRequestException;

import java.time.LocalDateTime;
import java.util.UUID;

public class Friend {
    private final UUID userId1;
    private final UUID userId2;
    private FriendStatus status;
    private LocalDateTime requestDate;

    public Friend(
            UUID userId1,
            UUID userId2,
            FriendStatus status,
            LocalDateTime requestDate
    ) {
        if (userId1 == null || userId2 == null) {
            throw new InvalidFriendRequestException();
        }
        this.userId1 = userId1;
        this.userId2 = userId2;
        this.status = status;
        this.requestDate = requestDate;
    }

    public static Friend create(
            UUID userId1,
            UUID userId2
    ) {
        if (userId1 == null || userId2 == null) {
            throw new InvalidFriendRequestException();
        }
        if (userId1.equals(userId2)) {
            throw new InvalidFriendRequestException();
        }
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

    public boolean isParticipant(UUID userId) {
        return userId != null && (userId.equals(userId1) || userId.equals(userId2));
    }

    public UUID otherParty(UUID viewerId) {
        if (viewerId == null || !isParticipant(viewerId)) {
            throw new FriendNotFoundException();
        }
        return viewerId.equals(userId1) ? userId2 : userId1;
    }

    public boolean sentBy(UUID userId) {
        return userId != null && userId.equals(userId1);
    }

    public void accept(UUID userId) {
        if (userId == null || !userId.equals(userId2)) {
            throw new CanNotAcceptTheRequestException();
        }

        if (status.equals(FriendStatus.ACCEPT)) {
            return;
        }

        if (!status.equals(FriendStatus.PENDING)) {
            throw new InvalidFriendRequestException();
        }

        status = FriendStatus.ACCEPT;
    }

    public void decline(UUID userId) {
        if (userId == null || !userId.equals(userId2)) {
            throw new CanNotDeclineTheRequestException();
        }

        if (status.equals(FriendStatus.DECLINED)) {
            return;
        }

        if (!status.equals(FriendStatus.PENDING)) {
            throw new InvalidFriendRequestException();
        }

        status = FriendStatus.DECLINED;
    }

    public void remove(UUID actor) {
        if (actor == null || !isParticipant(actor)) {
            throw new InvalidFriendRequestException();
        }

        if (status.equals(FriendStatus.PENDING)) {
            throw new FriendRequestStillPendingException();
        }

        if (status.equals(FriendStatus.DECLINED)) {
            return;
        }

        status = FriendStatus.DECLINED;
    }

    public void cancel(UUID actor) {
        if (actor == null || !actor.equals(userId1)) {
            throw new CanNotDeclineTheRequestException();
        }

        if (status.equals(FriendStatus.DECLINED)) {
            return;
        }

        if (!status.equals(FriendStatus.PENDING)) {
            throw new InvalidFriendRequestException();
        }

        status = FriendStatus.DECLINED;
    }

    public void reopen() {
        if (status.equals(FriendStatus.PENDING)) {
            return;
        }

        if (!status.equals(FriendStatus.DECLINED)) {
            throw new InvalidFriendRequestException();
        }

        status = FriendStatus.PENDING;
        requestDate = LocalDateTime.now();
    }
}
