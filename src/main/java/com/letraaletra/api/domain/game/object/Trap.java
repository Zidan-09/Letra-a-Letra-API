package com.letraaletra.api.domain.game.object;

import com.letraaletra.api.domain.position.Position;

public class Trap {
    private final String ownerId;
    private final Position position;
    private boolean triggered;

    public Trap(String ownerId, Position position) {
        this.ownerId = ownerId;
        this.position = position;
        this.triggered = false;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public void trigger() {
        this.triggered = true;
        remove();
    }

    public void remove() {

    }
}
