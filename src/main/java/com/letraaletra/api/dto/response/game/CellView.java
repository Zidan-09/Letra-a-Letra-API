package com.letraaletra.api.dto.response.game;

public class CellView {
    private final boolean revealed;
    private final Character letter;
    private final String revealedById;

    public CellView(boolean revealed, Character letter, String revealedById) {
        this.revealed = revealed;
        this.letter = letter;
        this.revealedById = revealedById;
    }
}