package com.letraaletra.api.features.inventory.domain;

import com.letraaletra.api.features.inventory.domain.exception.InsufficientQuantityException;
import com.letraaletra.api.features.inventory.domain.exception.InvalidQuantityException;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserItem {
    private final UUID ownerId;
    private final UUID itemId;
    private int quantity;
    private boolean equipped;
    private final LocalDateTime acquiredAt;
    private final LocalDateTime expiresAt;

    public UserItem(
            UUID ownerId,
            UUID itemId,
            int quantity,
            boolean equipped,
            LocalDateTime acquiredAt,
            LocalDateTime expiresAt
    ) {
        this.ownerId = ownerId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.equipped = equipped;
        this.acquiredAt = acquiredAt;
        this.expiresAt = expiresAt;
    }

    public static UserItem create(
            UUID ownerId,
            UUID itemId,
            int quantity
    ) {
        if (ownerId == null || itemId == null) {
            throw new InvalidQuantityException();
        }

        if (quantity < 1) {
            throw new InvalidQuantityException();
        }

        return new UserItem(
                ownerId,
                itemId,
                quantity,
                false,
                LocalDateTime.now(),
                null
        );
    }

    public static UserItem restore(
            UUID ownerId,
            UUID itemId,
            int quantity,
            boolean equipped,
            LocalDateTime acquiredAt,
            LocalDateTime expiresAt
    ) {
        return new UserItem(
                ownerId,
                itemId,
                quantity,
                equipped,
                acquiredAt,
                expiresAt
        );
    }

    public void increase(int amount) {
        if (amount < 1) {
            throw new InvalidQuantityException();
        }

        this.quantity += amount;
    }

    public void decrease(int amount) {
        if (amount < 1) {
            throw new InvalidQuantityException();
        }

        if (amount > this.quantity) {
            throw new InsufficientQuantityException();
        }

        this.quantity -= amount;
    }

    public void markEquipped() {
        this.equipped = true;
    }

    public void markUnequipped() {
        this.equipped = false;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public UUID getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isEquipped() {
        return equipped;
    }

    public LocalDateTime getAcquiredAt() {
        return acquiredAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
}
