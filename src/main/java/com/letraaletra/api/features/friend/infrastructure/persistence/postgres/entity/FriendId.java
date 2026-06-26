package com.letraaletra.api.features.friend.infrastructure.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
public class FriendId implements Serializable {

    @Column(name = "user_id_1")
    private UUID userId1;

    @Column(name = "user_id_2")
    private UUID userId2;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FriendId that)) return false;
        return Objects.equals(userId1, that.userId1) &&
                Objects.equals(userId2, that.userId2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId1, userId2);
    }
}
