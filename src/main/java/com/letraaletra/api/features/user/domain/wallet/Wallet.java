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

    public WalletMovement add(CoinType coinType, int value) {
        Balance balanceBefore = getBalance();

        switch (coinType) {
            case SOFT -> {
                if (softCoins < value) {
                    throw new InsufficientBalanceException();
                }

                softCoins += value;

                return new WalletMovement(CoinType.SOFT, balanceBefore, getBalance(), value, OperationType.CREDIT);
            }
            case HARD -> {
                if (hardGems < value) {
                    throw new InsufficientBalanceException();
                }

                hardGems += value;

                return new WalletMovement(CoinType.HARD, balanceBefore, getBalance(), value, OperationType.CREDIT);
            }
            case null, default -> throw new InvalidPaymentException();
        }
    }

    public WalletMovement remove(CoinType coinType, int value) {
        Balance balanceBefore = getBalance();

        switch (coinType) {
            case SOFT -> {
                if (softCoins < value) {
                    throw new InsufficientBalanceException();
                }

                softCoins -= value;

                return new WalletMovement(CoinType.SOFT, balanceBefore, getBalance(), value, OperationType.DEBIT);
            }
            case HARD -> {
                if (hardGems < value) {
                    throw new InsufficientBalanceException();
                }

                hardGems -= value;

                return new WalletMovement(CoinType.HARD, balanceBefore, getBalance(), value, OperationType.DEBIT);
            }
            case null, default -> throw new InvalidPaymentException();
        }
    }
}
