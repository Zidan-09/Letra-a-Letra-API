package com.letraaletra.api.features.items.domain;

import com.letraaletra.api.features.items.domain.exception.InvalidItemStatusException;

import java.util.UUID;

public abstract class Item {
    private final UUID itemId;
    private String name;
    private int version;
    private boolean available;

    protected Item(
            UUID itemId,
            String name,
            int version,
            boolean available
    ) {
        this.itemId = itemId;
        this.name = name;
        this.version = version;
        this.available = available;
    }

    public UUID getId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public boolean isAvailable() {
        return available;
    }

    public void incrementVersion() {
        this.version++;
    }

    public void enable() {
        if (available) {
            throw new InvalidItemStatusException();
        }

        available = true;
    }

    public void disable() {
        if (!available) {
            throw new InvalidItemStatusException();
        }

        available = false;
    }
}
