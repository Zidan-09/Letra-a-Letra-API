package com.letraaletra.api.features.game.domain.board.word;

import com.letraaletra.api.features.game.domain.board.position.Position;

import java.util.List;
import java.util.UUID;

public class Word {
    private final String value;
    private final List<Position> positions;

    private boolean found;
    private UUID foundById;

    public Word(String value, List<Position> positions) {
        this.value = value;
        this.positions = List.copyOf(positions);
        this.found = false;
    }

    public String getValue() {
        return value;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public boolean isFound() {
        return found;
    }

    public UUID getFoundById() {
        return foundById;
    }

    public boolean markAsFound(UUID playerId) {
        if (found) {
            return false;
        }

        this.found = true;
        this.foundById = playerId;
        return true;
    }
}
