package com.letraaletra.api.domain.game;

public enum GameMode {
    EASY(new int[][]{{0, 1}, {1, 0}}),
    NORMAL(new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}}),
    HARD(new int[][]{{0, 1}, {1, 0}, {1, 1}, {-1, 0}, {0, -1}, {1, -1}}),
    INSANE(new int[][]{{0, 1}, {1, 0}, {1, 1}, {-1, 0}, {0, -1}, {-1, -1}, {1, -1}, {-1, 1}}),
    CATACLYSM(new int[][]{{0, 1}, {1, 0}, {1, 1}, {-1, 0}, {0, -1}, {-1, -1}, {1, -1}, {-1, 1}});

    private final int[][] directions;

    GameMode(int[][] directions) {
        this.directions = directions;
    }

    public int[][] getDirections() {
        return directions;
    }
}