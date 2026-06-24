package com.letraaletra.api.features.game.domain.board.cell;

import com.letraaletra.api.features.game.domain.board.cell.effect.CellEffect;
import com.letraaletra.api.features.game.domain.board.cell.exception.CellAlreadyRevealedException;
import com.letraaletra.api.features.game.domain.board.word.Word;
import com.letraaletra.api.features.game.domain.board.position.Position;
import com.letraaletra.api.features.power.domain.PowerType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cell {
    private final char letter;
    private final Position position;
    private boolean revealed;
    private UUID revealedById;
    private final List<Word> relatedWords = new ArrayList<>();
    private PowerType drop;

    private CellEffect effect;

    public Cell(char letter, Position position, PowerType drop) {
        this.letter = letter;
        this.position = position;
        this.revealed = false;
        this.drop = drop;
    }

    public char getLetter() {
        return letter;
    }

    public Position getPosition() {
        return position;
    }

    public CellEffect getEffect() {
        return effect;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public UUID getRevealedById() {
        return revealedById;
    }

    public PowerType reveal(UUID actor) {
        if (revealed) {
            throw new CellAlreadyRevealedException();
        }

        this.revealed = true;
        this.revealedById = actor;
        PowerType drop = this.drop;
        this.drop = null;

        return drop;
    }

    public void addRelatedWord(Word word) {
        relatedWords.add(word);
    }

    public List<Word> getRelatedWords() {
        return List.copyOf(relatedWords);
    }

    public void setEffect(CellEffect effect) {
        this.effect = effect;
    }

    public void clearEffect() {
        this.effect = null;
    }

    public boolean hasEffect() {
        return effect != null;
    }
}
