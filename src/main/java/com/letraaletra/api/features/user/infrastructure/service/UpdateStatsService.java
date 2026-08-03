package com.letraaletra.api.features.user.infrastructure.service;

import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.domain.repository.LevelRepository;
import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.user.application.port.UserStatsService;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.features.user.domain.wallet.Balance;
import com.letraaletra.api.features.transaction.domain.TransactionReason;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;
import com.letraaletra.api.features.transaction.domain.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdateStatsService implements UserStatsService {
    private final LevelRepository levelRepository;
    private final TransactionRepository walletTransactionRepository;

    @Override
    public void update(User user, boolean isWinner) {
        user.registerMatchResult(isWinner);

        int maxLevel = levelRepository.findBiggestLevel();

        int experience = isWinner ? 10 * 3 : 10;

        int beforeLevel = user.getStats().getLevel();

        user.getStats().incrementExperience(experience, maxLevel == 0 ? 1 : maxLevel);

        int afterLevel = user.getStats().getLevel();

        if (afterLevel > beforeLevel) {
            Optional<Level> level = levelRepository.findByLevel(afterLevel);

            level.ifPresent(l -> l.getRewards().forEach(levelReward -> {
                Optional<WalletMovement> movement = levelReward.reward().deliver(user);

                movement.ifPresent(walletMovement -> walletTransactionRepository.save(
                        Transaction.create(
                                user.getUserId(),
                                walletMovement.coinType(),
                                walletMovement.amount(),
                                getBalance(walletMovement.balanceBefore(), walletMovement.coinType()),
                                getBalance(user.getWallet().getBalance(), walletMovement.coinType()),
                                walletMovement.operation(),
                                TransactionReason.LEVEL_UP,
                                l.getLevelId()
                        )
                ));
            }));
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
