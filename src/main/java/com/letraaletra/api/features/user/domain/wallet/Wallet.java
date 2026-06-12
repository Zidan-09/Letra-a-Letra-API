package com.letraaletra.api.features.user.domain.wallet;

public class Wallet {
    private int softCoins;
    private int hardGems;

    public Wallet(int softCoins, int hardGems) {
        this.softCoins = softCoins;
        this.hardGems = hardGems;
    }

    public Balance getBalance() {
        return new Balance(
                softCoins,
                hardGems
        );
    }

    public void addSoft(int value) {
        softCoins += value;
    }

    public void addHard(int value) {
        hardGems += value;
    }
}
