package com.letraaletra.api.features.user.infrastructure.service;

import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.domain.LevelReward;
import com.letraaletra.api.features.levels.domain.repository.LevelRepository;
import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.transaction.domain.OperationType;
import com.letraaletra.api.features.transaction.domain.Transaction;
import com.letraaletra.api.features.transaction.domain.TransactionReason;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.inventory.Inventory;
import com.letraaletra.api.features.user.domain.stats.UserStats;
import com.letraaletra.api.features.user.domain.wallet.Balance;
import com.letraaletra.api.features.user.domain.wallet.Wallet;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;
import com.letraaletra.api.shared.domain.rewards.CosmeticReward;
import com.letraaletra.api.shared.domain.rewards.Reward;
import com.letraaletra.api.shared.domain.rewards.SoftCoinsReward;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateStatsService Unit Tests")
class UpdateStatsServiceTest {

    @Mock
    private LevelRepository levelRepository;

    @Mock
    private TransactionRepository walletTransactionRepository;

    @InjectMocks
    private UpdateStatsService service;

    @Captor
    private ArgumentCaptor<Transaction> transactionCaptor;

    private User mockUser;
    private UserStats mockUserStats;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        mockUser = mock(User.class);
        mockUserStats = mock(UserStats.class);
    }

    @Nested
    @DisplayName("sucesso no Fluxo Principal - Sem subida de Nível")
    class SuccessFlowsWithoutLevelUp {

        @Test
        @DisplayName("Deve registrar vitória, adicionar 30 de EXP e não gerar recompensa se o nível não subir")
        void update_WhenUserWinsAndDoesNotLevelUp_ShouldRegisterWinAndIncrementExp() {
            boolean isWinner = true;
            int maxLevel = 10;
            int initialLevel = 2;

            when(mockUser.getStats()).thenReturn(mockUserStats);
            when(levelRepository.findBiggestLevel()).thenReturn(maxLevel);
            when(mockUserStats.getLevel())
                    .thenReturn(initialLevel)
                    .thenReturn(initialLevel);

            service.update(mockUser, isWinner);

            verify(mockUser).registerMatchResult(true);
            verify(levelRepository).findBiggestLevel();
            verify(mockUserStats).incrementExperience(30, maxLevel);

            verify(levelRepository, never())
                    .findByLevel(any(Integer.class));

            verifyNoInteractions(walletTransactionRepository);
        }

        @Test
        @DisplayName("Deve registrar derrota, adicionar 10 de EXP e usar 1 como maxLevel padrão se findBiggestLevel retornar 0")
        void update_WhenUserLosesAndMaxLevelIsZero_ShouldUseDefaultMaxLevelOne() {
            boolean isWinner = false;
            int maxLevelFromRepo = 0;
            int initialLevel = 1;

            when(mockUser.getStats()).thenReturn(mockUserStats);
            when(levelRepository.findBiggestLevel()).thenReturn(maxLevelFromRepo);
            when(mockUserStats.getLevel())
                    .thenReturn(initialLevel)
                    .thenReturn(initialLevel);

            service.update(mockUser, isWinner);

            verify(mockUser).registerMatchResult(false);
            verify(levelRepository).findBiggestLevel();
            verify(mockUserStats).incrementExperience(10, 1);

            verify(levelRepository, never())
                    .findByLevel(any(Integer.class));

            verifyNoInteractions(walletTransactionRepository);
        }
    }

    @Nested
    @DisplayName("sucesso no Fluxo Principal - Com subida de Nível e Recompensas")
    class SuccessFlowsWithLevelUp {

        @Test
        @DisplayName("Deve processar recompensas e salvar transações quando o usuário subir de nível")
        void update_WhenUserLevelsUp_ShouldApplyRewardsAndSaveTransactions() {
            boolean isWinner = true;
            int maxLevel = 50;
            int beforeLevel = 1;
            int afterLevel = 2;
            UUID levelId = UUID.randomUUID();

            Wallet wallet = mock(Wallet.class);

            when(mockUser.getUserId()).thenReturn(userId);
            when(mockUser.getStats()).thenReturn(mockUserStats);
            when(mockUser.getWallet()).thenReturn(wallet);

            when(levelRepository.findBiggestLevel())
                    .thenReturn(maxLevel);

            when(mockUserStats.getLevel())
                    .thenReturn(beforeLevel)
                    .thenReturn(afterLevel);

            Balance balanceBefore = new Balance(100, 10);
            Balance balanceAfter = new Balance(200, 10);

            WalletMovement movement = new WalletMovement(
                    CoinType.SOFT,
                    balanceBefore,
                    balanceAfter,
                    100,
                    OperationType.CREDIT
            );

            when(wallet.add(CoinType.SOFT, 100))
                    .thenReturn(movement);

            Reward reward = new SoftCoinsReward(100);

            LevelReward levelReward = new LevelReward(
                    levelId,
                    reward
            );

            Level level = new Level(
                    levelId,
                    afterLevel,
                    List.of(levelReward)
            );

            when(levelRepository.findByLevel(afterLevel))
                    .thenReturn(Optional.of(level));

            service.update(mockUser, isWinner);

            verify(mockUser).registerMatchResult(true);
            verify(mockUserStats).incrementExperience(30, maxLevel);
            verify(levelRepository).findByLevel(afterLevel);

            verify(wallet).add(CoinType.SOFT, 100);

            verify(walletTransactionRepository)
                    .save(transactionCaptor.capture());

            Transaction captured = transactionCaptor.getValue();

            assertNotNull(captured);
            assertNotNull(captured.transactionId());
            assertEquals(userId, captured.userId());
            assertEquals(CoinType.SOFT, captured.coinType());
            assertEquals(100, captured.amount());
            assertEquals(100, captured.balanceBefore());
            assertEquals(200, captured.balanceAfter());
            assertEquals(OperationType.CREDIT, captured.operation());
            assertEquals(TransactionReason.LEVEL_UP, captured.reason());
            assertEquals(levelId, captured.referenceId());
            assertNotNull(captured.createdAt());
        }

        @Test
        @DisplayName("Não deve salvar transação se o Reward.apply retornar Optional.empty()")
        void update_WhenLevelUpRewardReturnsEmptyMovement_ShouldNotSaveTransaction() {
            boolean isWinner = true;
            int maxLevel = 20;
            int beforeLevel = 1;
            int afterLevel = 2;
            UUID levelId = UUID.randomUUID();

            Inventory inventory = mock(Inventory.class);

            when(mockUser.getStats()).thenReturn(mockUserStats);
            when(mockUser.getInventory()).thenReturn(inventory);

            when(levelRepository.findBiggestLevel())
                    .thenReturn(maxLevel);

            when(mockUserStats.getLevel())
                    .thenReturn(beforeLevel)
                    .thenReturn(afterLevel);

            Reward reward = new CosmeticReward(null);

            LevelReward levelReward = new LevelReward(
                    levelId,
                    reward
            );

            Level level = new Level(
                    levelId,
                    afterLevel,
                    List.of(levelReward)
            );

            when(levelRepository.findByLevel(afterLevel))
                    .thenReturn(Optional.of(level));

            service.update(mockUser, isWinner);

            verify(mockUser).registerMatchResult(true);
            verify(mockUserStats).incrementExperience(30, maxLevel);
            verify(levelRepository).findByLevel(afterLevel);

            verify(inventory).unlock(null);

            verifyNoInteractions(walletTransactionRepository);
        }

        @Test
        @DisplayName("Não deve processar recompensas se o nível subir mas o Level não for encontrado no repositório")
        void update_WhenLevelUpButLevelNotFoundInRepository_ShouldNotThrowAndNotSaveTransaction() {
            boolean isWinner = true;
            int maxLevel = 20;
            int beforeLevel = 1;
            int afterLevel = 2;

            when(mockUser.getStats()).thenReturn(mockUserStats);

            when(levelRepository.findBiggestLevel())
                    .thenReturn(maxLevel);

            when(mockUserStats.getLevel())
                    .thenReturn(beforeLevel)
                    .thenReturn(afterLevel);

            when(levelRepository.findByLevel(afterLevel))
                    .thenReturn(Optional.empty());

            service.update(mockUser, isWinner);

            verify(mockUser).registerMatchResult(true);
            verify(mockUserStats).incrementExperience(30, maxLevel);
            verify(levelRepository).findByLevel(afterLevel);

            verifyNoInteractions(walletTransactionRepository);
        }
    }

    @Nested
    @DisplayName("Falhas na Camada de Repositório")
    class RepositoryFailures {

        @Test
        @DisplayName("Deve propagar exceção caso LevelRepository.findBiggestLevel falhe")
        void update_WhenFindBiggestLevelFails_ShouldPropagateException() {
            when(levelRepository.findBiggestLevel())
                    .thenThrow(new RuntimeException(
                            "Erro ao buscar maior nível"
                    ));

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> service.update(mockUser, true)
            );

            assertEquals(
                    "Erro ao buscar maior nível",
                    exception.getMessage()
            );

            verify(mockUser).registerMatchResult(true);
            verify(levelRepository).findBiggestLevel();

            verifyNoInteractions(walletTransactionRepository);
        }

        @Test
        @DisplayName("Deve propagar exceção caso TransactionRepository.save falhe")
        void update_WhenTransactionRepositorySaveFails_ShouldPropagateException() {
            boolean isWinner = true;
            int maxLevel = 10;
            int beforeLevel = 1;
            int afterLevel = 2;
            UUID levelId = UUID.randomUUID();

            Wallet wallet = mock(Wallet.class);

            when(mockUser.getUserId()).thenReturn(userId);
            when(mockUser.getStats()).thenReturn(mockUserStats);
            when(mockUser.getWallet()).thenReturn(wallet);

            when(levelRepository.findBiggestLevel())
                    .thenReturn(maxLevel);

            when(mockUserStats.getLevel())
                    .thenReturn(beforeLevel)
                    .thenReturn(afterLevel);

            Balance balanceBefore = new Balance(0, 0);
            Balance balanceAfter = new Balance(50, 0);

            WalletMovement movement = new WalletMovement(
                    CoinType.SOFT,
                    balanceBefore,
                    balanceAfter,
                    50,
                    OperationType.CREDIT
            );

            when(wallet.add(CoinType.SOFT, 50))
                    .thenReturn(movement);

            Reward reward = new SoftCoinsReward(50);

            LevelReward levelReward = new LevelReward(
                    levelId,
                    reward
            );

            Level level = new Level(
                    levelId,
                    afterLevel,
                    List.of(levelReward)
            );

            when(levelRepository.findByLevel(afterLevel))
                    .thenReturn(Optional.of(level));

            doThrow(new RuntimeException(
                    "Erro de persistência na transação"
            ))
                    .when(walletTransactionRepository)
                    .save(any(Transaction.class));

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> service.update(mockUser, isWinner)
            );

            assertEquals(
                    "Erro de persistência na transação",
                    exception.getMessage()
            );

            verify(wallet).add(CoinType.SOFT, 50);

            verify(walletTransactionRepository)
                    .save(any(Transaction.class));
        }
    }
}