package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.admin.domain.exception.PermissionDeniedException;
import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.offers.domain.RewardType;
import com.letraaletra.api.features.transaction.domain.OperationType;
import com.letraaletra.api.features.transaction.domain.Transaction;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.features.user.application.input.GrantUserRewardInput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.features.user.domain.wallet.Balance;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.port.RewardFactory;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.domain.rewards.CosmeticReward;
import com.letraaletra.api.shared.domain.rewards.SoftCoinsReward;
import com.letraaletra.api.shared.domain.security.exceptions.UserIsNotAdminException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GrantUserRewardUseCase Unit Tests")
class GrantUserRewardUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AdminChecker adminChecker;

    @Mock
    private RewardFactory rewardFactory;

    @InjectMocks
    private GrantUserRewardUseCase useCase;

    @Captor
    private ArgumentCaptor<Transaction> transactionCaptor;

    private AuthenticatedUser principal;
    private UUID targetUserId;
    private UUID cosmeticId;
    private User mockUser;

    @BeforeEach
    void setUp() {
        UUID adminAuthId = UUID.randomUUID();

        principal = new AuthenticatedUser(
                adminAuthId,
                "AdminUser",
                true,
                false
        );

        targetUserId = UUID.randomUUID();
        cosmeticId = UUID.randomUUID();

        mockUser = mock(User.class);
    }

    @Nested
    @DisplayName("sucesso no Fluxo Principal")
    class SuccessFlows {

        @Test
        @DisplayName("Deve conceder recompensa com movimentação de carteira e registrar transação no repositório")
        void execute_WhenRewardGeneratesWalletMovement_ShouldSaveTransactionAndUser() {
            GrantUserRewardInput input = new GrantUserRewardInput(
                    principal,
                    targetUserId,
                    RewardType.COIN,
                    null,
                    100
            );

            CoinType coinType = CoinType.SOFT;
            OperationType operation = OperationType.CREDIT;

            Balance balanceBefore = mock(Balance.class);
            Balance balanceAfter = mock(Balance.class);

            when(balanceBefore.getAmountFor(coinType)).thenReturn(50L);
            when(balanceAfter.getAmountFor(coinType)).thenReturn(150L);

            WalletMovement walletMovement = new WalletMovement(
                    coinType,
                    balanceBefore,
                    balanceAfter,
                    100,
                    operation
            );

            SoftCoinsReward reward = mock(SoftCoinsReward.class);

            when(userRepository.find(targetUserId))
                    .thenReturn(Optional.of(mockUser));

            when(rewardFactory.create(
                    RewardType.COIN,
                    100,
                    null
            )).thenReturn(reward);

            when(reward.apply(mockUser))
                    .thenReturn(Optional.of(walletMovement));

            when(mockUser.getUserId())
                    .thenReturn(targetUserId);

            Void result = useCase.execute(input);

            assertNull(result);

            InOrder inOrder = inOrder(
                    adminChecker,
                    userRepository,
                    rewardFactory,
                    reward,
                    transactionRepository
            );

            inOrder.verify(adminChecker)
                    .check(
                            principal,
                            PermissionKey.USER,
                            PermissionAction.EDIT
                    );

            inOrder.verify(userRepository)
                    .find(targetUserId);

            inOrder.verify(rewardFactory)
                    .create(
                            RewardType.COIN,
                            100,
                            null
                    );

            inOrder.verify(reward)
                    .apply(mockUser);

            inOrder.verify(transactionRepository)
                    .save(transactionCaptor.capture());

            inOrder.verify(userRepository)
                    .save(mockUser);

            Transaction savedTransaction = transactionCaptor.getValue();

            assertNotNull(savedTransaction);
        }

        @Test
        @DisplayName("Deve conceder recompensa sem movimentação de carteira (ex: cosmético) e não gerar transação")
        void execute_WhenRewardHasNoWalletMovement_ShouldSaveUserWithoutTransaction() {
            GrantUserRewardInput input = new GrantUserRewardInput(
                    principal,
                    targetUserId,
                    RewardType.COSMETIC,
                    cosmeticId,
                    null
            );

            Cosmetic cosmetic = mock(Cosmetic.class);
            CosmeticReward reward = mock(CosmeticReward.class);

            when(userRepository.find(targetUserId))
                    .thenReturn(Optional.of(mockUser));

            when(rewardFactory.create(
                    RewardType.COSMETIC,
                    null,
                    cosmeticId
            )).thenReturn(reward);

            when(reward.apply(mockUser))
                    .thenReturn(Optional.empty());

            Void result = useCase.execute(input);

            assertNull(result);

            verify(adminChecker, times(1))
                    .check(
                            principal,
                            PermissionKey.USER,
                            PermissionAction.EDIT
                    );

            verify(userRepository, times(1))
                    .find(targetUserId);

            verify(rewardFactory, times(1))
                    .create(
                            RewardType.COSMETIC,
                            null,
                            cosmeticId
                    );

            verify(reward, times(1))
                    .apply(mockUser);

            verify(userRepository, times(1))
                    .save(mockUser);

            verifyNoInteractions(transactionRepository);
        }
    }

    @Nested
    @DisplayName("Autorização e Validação de Permissões")
    class AuthorizationAndPermissions {

        @Test
        @DisplayName("Deve lançar UserIsNotAdminException quando o principal não for um usuário admin")
        void execute_WhenUserIsNotAdmin_ShouldThrowUserIsNotAdminExceptionAndNotProcess() {
            GrantUserRewardInput input = new GrantUserRewardInput(
                    principal,
                    targetUserId,
                    RewardType.COIN,
                    null,
                    50
            );

            doThrow(new UserIsNotAdminException())
                    .when(adminChecker)
                    .check(
                            principal,
                            PermissionKey.USER,
                            PermissionAction.EDIT
                    );

            assertThrows(
                    UserIsNotAdminException.class,
                    () -> useCase.execute(input)
            );

            verify(adminChecker, times(1))
                    .check(
                            principal,
                            PermissionKey.USER,
                            PermissionAction.EDIT
                    );

            verifyNoInteractions(
                    userRepository,
                    transactionRepository,
                    rewardFactory
            );
        }

        @Test
        @DisplayName("Deve lançar PermissionDeniedException quando o admin não possuir permissão EDIT em USER")
        void execute_WhenAdminLacksEditPermission_ShouldThrowPermissionDeniedException() {
            GrantUserRewardInput input = new GrantUserRewardInput(
                    principal,
                    targetUserId,
                    RewardType.GEMS,
                    null,
                    200
            );

            doThrow(new PermissionDeniedException())
                    .when(adminChecker)
                    .check(
                            principal,
                            PermissionKey.USER,
                            PermissionAction.EDIT
                    );

            assertThrows(
                    PermissionDeniedException.class,
                    () -> useCase.execute(input)
            );

            verify(adminChecker, times(1))
                    .check(
                            principal,
                            PermissionKey.USER,
                            PermissionAction.EDIT
                    );

            verifyNoInteractions(
                    userRepository,
                    transactionRepository,
                    rewardFactory
            );
        }
    }

    @Nested
    @DisplayName("Exceções de Domínio e Busca de Usuário")
    class DomainExceptions {

        @Test
        @DisplayName("Deve lançar UserNotFoundException quando o usuário destino não for encontrado no banco")
        void execute_WhenUserNotFound_ShouldThrowUserNotFoundExceptionAndNotCreateReward() {
            GrantUserRewardInput input = new GrantUserRewardInput(
                    principal,
                    targetUserId,
                    RewardType.COIN,
                    null,
                    100
            );

            when(userRepository.find(targetUserId))
                    .thenReturn(Optional.empty());

            assertThrows(
                    UserNotFoundException.class,
                    () -> useCase.execute(input)
            );

            verify(adminChecker, times(1))
                    .check(
                            principal,
                            PermissionKey.USER,
                            PermissionAction.EDIT
                    );

            verify(userRepository, times(1))
                    .find(targetUserId);

            verifyNoInteractions(
                    rewardFactory,
                    transactionRepository
            );

            verify(userRepository, never())
                    .save(any());
        }
    }

    @Nested
    @DisplayName("Falhas de Infraestrutura e Fábrica")
    class InfrastructureFailures {

        @Test
        @DisplayName("Deve propagar exceção quando a RewardFactory falhar na criação do Reward")
        void execute_WhenRewardFactoryFails_ShouldPropagateExceptionAndNotSaveUser() {
            GrantUserRewardInput input = new GrantUserRewardInput(
                    principal,
                    targetUserId,
                    RewardType.COIN,
                    null,
                    -10
            );

            when(userRepository.find(targetUserId))
                    .thenReturn(Optional.of(mockUser));

            doThrow(new IllegalArgumentException(
                    "Quantidade de moedas deve ser positiva"
            ))
                    .when(rewardFactory)
                    .create(
                            RewardType.COIN,
                            -10,
                            null
                    );

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> useCase.execute(input)
            );

            assertEquals(
                    "Quantidade de moedas deve ser positiva",
                    exception.getMessage()
            );

            verify(adminChecker, times(1))
                    .check(
                            principal,
                            PermissionKey.USER,
                            PermissionAction.EDIT
                    );

            verify(userRepository, times(1))
                    .find(targetUserId);

            verify(rewardFactory, times(1))
                    .create(
                            RewardType.COIN,
                            -10,
                            null
                    );

            verify(userRepository, never())
                    .save(any());

            verifyNoInteractions(transactionRepository);
        }

        @Test
        @DisplayName("Deve propagar exceção caso o UserRepository falhe ao salvar as alterações do usuário")
        void execute_WhenUserRepositorySaveFails_ShouldPropagateException() {
            GrantUserRewardInput input = new GrantUserRewardInput(
                    principal,
                    targetUserId,
                    RewardType.COSMETIC,
                    cosmeticId,
                    null
            );

            CosmeticReward reward = mock(CosmeticReward.class);

            when(userRepository.find(targetUserId))
                    .thenReturn(Optional.of(mockUser));

            when(rewardFactory.create(
                    RewardType.COSMETIC,
                    null,
                    cosmeticId
            )).thenReturn(reward);

            when(reward.apply(mockUser))
                    .thenReturn(Optional.empty());

            doThrow(new RuntimeException(
                    "Erro ao conectar ao banco de dados"
            ))
                    .when(userRepository)
                    .save(mockUser);

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> useCase.execute(input)
            );

            assertEquals(
                    "Erro ao conectar ao banco de dados",
                    exception.getMessage()
            );

            verify(userRepository, times(1))
                    .save(mockUser);
        }
    }
}
