package com.letraaletra.api.domain.power;

public abstract class Power {
    private final String name;
    private final PowerRarity rarity;

    public Power(String name, PowerRarity rarity) {
        this.name = name;
        this.rarity = rarity;
    }

    public String getName() {
        return name;
    }

    public PowerRarity getRarity() {
        return rarity;
    }

}
