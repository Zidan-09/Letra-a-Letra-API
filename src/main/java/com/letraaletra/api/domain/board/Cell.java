package com.letraaletra.api.domain.board;

import com.letraaletra.api.domain.position.Position;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private final char letter;
    private final Position position;
    private boolean revealed;
    private String revealedById;
    private final List<Word> relatedWords = new ArrayList<>();


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

    public void addRelatedWord(Word word) {
        relatedWords.add(word);
    }

    public List<Word> getRelatedWords() {
        return List.copyOf(relatedWords);
    }

}
