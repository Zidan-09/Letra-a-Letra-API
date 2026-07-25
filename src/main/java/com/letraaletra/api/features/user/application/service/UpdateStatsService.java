package com.letraaletra.api.features.user.application.service;

import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.domain.exception.LevelNotFoundException;
import com.letraaletra.api.features.levels.domain.repository.LevelRepository;
import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.WalletTransactionRepository;
import com.letraaletra.api.features.user.domain.wallet.Balance;
import com.letraaletra.api.features.user.domain.wallet.TransactionReason;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;
import com.letraaletra.api.features.user.domain.wallet.WalletTransaction;

import java.util.Optional;

public class UpdateStatsService {
    private final UserRepository userRepository;
    private final LevelRepository levelRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public UpdateStatsService(
            UserRepository userRepository,
            LevelRepository levelRepository,
            WalletTransactionRepository walletTransactionRepository
    ) {
        this.userRepository = userRepository;
        this.levelRepository = levelRepository;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    public void execute(Player player, boolean isWinner) {
        User user = userRepository.find(player.getUserId())
                .orElseThrow(UserNotFoundException::new);

        user.registerMatchResult(isWinner);

        int maxLevel = levelRepository.findBiggestLevel();

        int experience = isWinner ? 10 * 3 : 10;

        int beforeLevel = user.getStats().getLevel();

        user.getStats().incrementExperience(experience, maxLevel);

        int afterLevel = user.getStats().getLevel();

        if (afterLevel > beforeLevel) {
            Level level = levelRepository.findByLevel(afterLevel)
                    .orElseThrow(LevelNotFoundException::new);

            level.getRewards().forEach(levelReward -> {
                Balance balanceBefore = user.getWallet().getBalance();

                Optional<WalletMovement> movement = levelReward.reward().deliver(user);

                movement.ifPresent(walletMovement -> walletTransactionRepository.save(
                        WalletTransaction.create(
                                user.getId(),
                                walletMovement.coinType(),
                                walletMovement.amount(),
                                getBalance(balanceBefore, walletMovement.coinType()),
                                getBalance(user.getWallet().getBalance(), walletMovement.coinType()),
                                walletMovement.operation(),
                                TransactionReason.LEVEL_UP,
                                level.getLevelId()
                        )
                ));
            });
        }
    }

    private int getBalance(Balance balance, CoinType coinType) {
        return switch (coinType) {
            case SOFT -> (int) balance.coins();
            case HARD -> (int) balance.gems();
            case REAL -> throw new IllegalStateException(
                    "Wallet movements cannot use REAL coin type."
            );
        };
    }
}
