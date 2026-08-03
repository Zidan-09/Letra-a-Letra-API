package com.letraaletra.api.features.user.domain.wallet;

import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.offers.domain.exception.InvalidPaymentException;
import com.letraaletra.api.features.transaction.domain.OperationType;
import com.letraaletra.api.features.user.domain.exception.InsufficientBalanceException;

public class Wallet {
    private long softCoins;
    private long hardGems;

    private Wallet(long softCoins, long hardGems) {
        this.softCoins = softCoins;
        this.hardGems = hardGems;
    }

    public static Wallet create() {
        return new Wallet(
                0,
                0
        );
    }

    public static Wallet restore(
            long softCoins,
            long hardGems
    ) {
        return new Wallet(
                softCoins,
                hardGems
        );
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

    public WalletMovement pay(CoinType coinType, int value) {
        Balance balanceBefore = getBalance();

        switch (coinType) {
            case SOFT -> {
                if (softCoins < value) {
                    throw new InsufficientBalanceException();
                }
                removeSoft(value);

                return new WalletMovement(CoinType.SOFT, balanceBefore, value, OperationType.DEBIT);
            }
            case HARD -> {
                if (hardGems < value) {
                    throw new InsufficientBalanceException();
                }
                removeHard(value);

                return new WalletMovement(CoinType.HARD, balanceBefore, value, OperationType.DEBIT);
            }
            case null, default -> throw new InvalidPaymentException();
        }
    }
}
