package com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
public class UserItemId implements Serializable {
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "item_id")
    private UUID itemId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserItemId that)) return false;
        return Objects.equals(itemId, that.itemId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, itemId);
    }
}
