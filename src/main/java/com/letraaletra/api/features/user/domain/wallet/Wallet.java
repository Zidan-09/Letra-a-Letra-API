package com.letraaletra.api.features.user.domain.wallet;

import com.letraaletra.api.features.user.domain.exception.InsufficientBalanceException;

public class Wallet {
    private long softCoins;
    private long hardGems;

    public Wallet(long softCoins, long hardGems) {
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

    private void removeSoft(long value) {
        softCoins -= value;
    }

    private void removeHard(long value) {
        hardGems -= value;
    }

    public void pay(CoinType coinType, long amount) {
        switch (coinType) {
            case SOFT -> {
                if (softCoins < amount) {
                    throw new InsufficientBalanceException();
                }
                removeSoft(amount);
            }
            case HARD -> {
                if (hardGems < amount) {
                    throw new InsufficientBalanceException();
                }
                removeHard(amount);
            }
        }
    }
}
