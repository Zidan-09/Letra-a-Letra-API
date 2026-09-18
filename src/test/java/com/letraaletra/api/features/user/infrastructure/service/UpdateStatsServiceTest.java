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
import com.letraaletra.api.features.user.domain.stats.UserStats;
import com.letraaletra.api.features.user.domain.wallet.Balance;
import com.letraaletra.api.features.user.domain.wallet.Wallet;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;
import com.letraaletra.api.features.reward.domain.ItemGrantReward;
import com.letraaletra.api.features.reward.domain.Reward;
import com.letraaletra.api.features.reward.domain.SoftCoinsReward;
import com.letraaletra.api.features.items.domain.ConsumableItem;
import com.letraaletra.api.features.items.domain.EffectType;
import com.letraaletra.api.features.items.domain.EquippableCategory;
import com.letraaletra.api.features.items.domain.EquippableContext;
import com.letraaletra.api.features.items.domain.PercentageTimedEffect;
import com.letraaletra.api.features.user.domain.effect.ActiveEffects;
import com.letraaletra.api.features.user.domain.effect.effects.ExperienceBonusEffect;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.items.domain.repository.ItemRepository;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
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

    @Mock
    private com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder auditRecorder;

    @Mock
    private com.letraaletra.api.shared.application.port.OperationContext operationContext;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private InventoryRepository inventoryRepository;

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

        lenient().when(operationContext.currentOperationId()).thenReturn(Optional.empty());
        lenient().when(walletTransactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(mockUser.getActiveEffects()).thenReturn(ActiveEffects.create());
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
        @DisplayName("Deve aplicar bonus de XP com efeito temporal valido e remover o expirado")
        void update_WhenValidXpBoost_ShouldApplyBonusAndPruneExpired() {
            boolean isWinner = true;
            int maxLevel = 10;

            ActiveEffects effects = ActiveEffects.create();
            effects.add(new ExperienceBonusEffect(50, java.time.Instant.now().plusSeconds(3600)));
            effects.add(new ExperienceBonusEffect(50, java.time.Instant.now().minusSeconds(1)));
            when(mockUser.getActiveEffects()).thenReturn(effects);
            when(mockUser.getStats()).thenReturn(mockUserStats);
            when(levelRepository.findBiggestLevel()).thenReturn(maxLevel);
            when(mockUserStats.getLevel()).thenReturn(2).thenReturn(2);

            service.update(mockUser, isWinner);

            verify(mockUserStats).incrementExperience(45, maxLevel);
            assertEquals(1, effects.getEffects().size());
        }

        @Test
        @DisplayName("Deve ignorar bonus de XP expirado")
        void update_WhenExpiredXpBoost_ShouldIgnoreBonus() {
            boolean isWinner = false;
            int maxLevel = 10;

            ActiveEffects effects = ActiveEffects.create();
            effects.add(new ExperienceBonusEffect(50, java.time.Instant.now().minusSeconds(1)));
            when(mockUser.getActiveEffects()).thenReturn(effects);
            when(mockUser.getStats()).thenReturn(mockUserStats);
            when(levelRepository.findBiggestLevel()).thenReturn(maxLevel);
            when(mockUserStats.getLevel()).thenReturn(2).thenReturn(2);

            service.update(mockUser, isWinner);

            verify(mockUserStats).incrementExperience(10, maxLevel);
            assertTrue(effects.isEmpty());
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
        @DisplayName("Deve conceder ItemGrantReward no inventário novo ao subir de nível")
        void update_WhenLevelUpWithItemGrantReward_ShouldGrantToNewInventory() {
            boolean isWinner = true;
            int maxLevel = 20;
            int beforeLevel = 1;
            int afterLevel = 2;
            UUID levelId = UUID.randomUUID();

            ConsumableItem boost = ConsumableItem.create(
                    "XP Boost 50%",
                    
                    new PercentageTimedEffect(EffectType.XP_BOOST_PCT, 50, 60)
            );

            when(mockUser.getUserId()).thenReturn(userId);
            when(mockUser.getStats()).thenReturn(mockUserStats);

            when(levelRepository.findBiggestLevel())
                    .thenReturn(maxLevel);

            when(mockUserStats.getLevel())
                    .thenReturn(beforeLevel)
                    .thenReturn(afterLevel);

            Reward reward = new ItemGrantReward(boost.getId(), 2);

            Level level = new Level(
                    levelId,
                    afterLevel,
                    List.of(new LevelReward(levelId, reward))
            );

            when(levelRepository.findByLevel(afterLevel))
                    .thenReturn(Optional.of(level));
            when(itemRepository.findById(boost.getId()))
                    .thenReturn(Optional.of(boost));
            when(inventoryRepository.findItemsByOwner(userId))
                    .thenReturn(List.of());

            service.update(mockUser, isWinner);

            verify(inventoryRepository).deleteItemsByOwner(userId);
            verify(inventoryRepository).saveItem(eq(userId), any(UserItem.class));
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