package com.letraaletra.api.domain.board;

import com.letraaletra.api.domain.position.Position;

public class Cell {
    private final char letter;
    private final Position position;

    private boolean revealed;
    private String revealedById;


    public Cell(char letter, Position position) {
        this.letter = letter;
        this.position = position;
        this.revealed = false;
    }

    public char getLetter() {
        return letter;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public String getRevealedById() {
        return revealedById;
    }

    public void reveal(String actor) {
        this.revealed = true;
        this.revealedById = actor;
    }
}
