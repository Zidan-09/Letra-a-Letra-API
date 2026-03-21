package com.letraaletra.api.domain.board;

import com.letraaletra.api.domain.position.Position;
import com.letraaletra.api.domain.power.Power;
import com.letraaletra.api.domain.power.manipulation.ManipulationPower;

public class Cell {
    private final char letter;
    private final Position position;

    private boolean revealed;
    private String revealedById;

    private ManipulationPower appliedPower;
    private String appliedById;

    private final Power power;

    public Cell(char letter, Position position, Power power) {
        this.letter = letter;
        this.position = position;
        this.revealed = false;
        this.power = power;
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

    public boolean hasPower() {
        return power != null;
    }

    public Power getPower() {
        return power;
    }

    public void applyPower(ManipulationPower power, String appliedById) {
        this.appliedPower = power;
        this.appliedById = appliedById;
    }

    public void removePower() {
        this.appliedPower = null;
        this.appliedById = null;
    }

    public ManipulationPower getAppliedPower() {
        return appliedPower;
    }

    public String getAppliedById() {
        return appliedById;
    }

    public char reveal(String actor) {
        this.revealed = true;
        this.revealedById = actor;
        return letter;
    }
}
