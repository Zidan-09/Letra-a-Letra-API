package com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
public class UserInventoryId implements Serializable {
    private UUID userId;
    private UUID cosmeticId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserInventoryId that)) return false;
        return Objects.equals(cosmeticId, that.cosmeticId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, cosmeticId);
    }
}
