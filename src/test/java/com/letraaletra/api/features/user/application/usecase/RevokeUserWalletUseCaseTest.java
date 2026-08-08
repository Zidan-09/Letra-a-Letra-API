package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.admin.domain.exception.PermissionDeniedException;
import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.offers.domain.exception.InvalidPaymentException;
import com.letraaletra.api.features.transaction.domain.OperationType;
import com.letraaletra.api.features.transaction.domain.Transaction;
import com.letraaletra.api.features.transaction.domain.TransactionReason;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.features.user.application.input.RevokeUserWalletInput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.InsufficientBalanceException;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.features.user.domain.wallet.Balance;
import com.letraaletra.api.features.user.domain.wallet.Wallet;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
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
@DisplayName("RevokeUserWalletUseCase Unit Tests")
class RevokeUserWalletUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private RevokeUserWalletUseCase useCase;

    @Captor
    private ArgumentCaptor<Transaction> transactionCaptor;

    private AuthenticatedUser principal;
    private UUID adminAuthId;
    private UUID targetUserId;
    private User mockUser;
    private Wallet mockWallet;

    @BeforeEach
    void setUp() {
        adminAuthId = UUID.randomUUID();
        principal = new AuthenticatedUser(adminAuthId, "AdminUser", true, false);
        targetUserId = UUID.randomUUID();

        mockUser = mock(User.class);
        mockWallet = mock(Wallet.class);
    }

    @Nested
    @DisplayName("Sucesso no Fluxo Principal")
    class SuccessFlows {

        @Test
        @DisplayName("Deve revogar moeda SOFT da carteira, criar transação DEBIT com ADMIN_REVOKE e salvar entidades")
        void execute_WhenValidSoftCoinRevocation_ShouldDeductBalanceAndSaveTransaction() {
            int amountToRevoke = 100;
            RevokeUserWalletInput input = new RevokeUserWalletInput(principal, targetUserId, CoinType.SOFT, amountToRevoke);

            Balance balanceBefore = new Balance(500, 50);
            Balance balanceAfter = new Balance(400, 50);
            WalletMovement movement = new WalletMovement(CoinType.SOFT, balanceBefore, balanceAfter, amountToRevoke, OperationType.DEBIT);

            when(userRepository.find(targetUserId)).thenReturn(Optional.of(mockUser));
            when(mockUser.getWallet()).thenReturn(mockWallet);
            when(mockWallet.remove(CoinType.SOFT, amountToRevoke)).thenReturn(movement);

            Void result = useCase.execute(input);

            assertNull(result);

            InOrder inOrder = inOrder(adminChecker, userRepository, mockUser, mockWallet, transactionRepository);
            inOrder.verify(adminChecker).check(principal, PermissionKey.USER, PermissionAction.EDIT);
            inOrder.verify(userRepository).find(targetUserId);
            inOrder.verify(mockUser).getWallet();
            inOrder.verify(mockWallet).remove(CoinType.SOFT, amountToRevoke);
            inOrder.verify(userRepository).save(mockUser);
            inOrder.verify(transactionRepository).save(transactionCaptor.capture());

            Transaction capturedTransaction = transactionCaptor.getValue();
            assertNotNull(capturedTransaction.transactionId());
            assertEquals(targetUserId, capturedTransaction.userId());
            assertEquals(CoinType.SOFT, capturedTransaction.coinType());
            assertEquals(amountToRevoke, capturedTransaction.amount());
            assertEquals(500, capturedTransaction.balanceBefore());
            assertEquals(400, capturedTransaction.balanceAfter());
            assertEquals(OperationType.DEBIT, capturedTransaction.operation());
            assertEquals(TransactionReason.ADMIN_REVOKE, capturedTransaction.reason());
            assertEquals(adminAuthId, capturedTransaction.referenceId());
            assertNotNull(capturedTransaction.createdAt());
        }

        @Test
        @DisplayName("Deve revogar moeda HARD da carteira, criar transação DEBIT com ADMIN_REVOKE e salvar entidades")
        void execute_WhenValidHardCoinRevocation_ShouldDeductBalanceAndSaveTransaction() {
            int amountToRevoke = 20;
            RevokeUserWalletInput input = new RevokeUserWalletInput(principal, targetUserId, CoinType.HARD, amountToRevoke);

            Balance balanceBefore = new Balance(1000, 50);
            Balance balanceAfter = new Balance(1000, 30);
            WalletMovement movement = new WalletMovement(CoinType.HARD, balanceBefore, balanceAfter, amountToRevoke, OperationType.DEBIT);

            when(userRepository.find(targetUserId)).thenReturn(Optional.of(mockUser));
            when(mockUser.getWallet()).thenReturn(mockWallet);
            when(mockWallet.remove(CoinType.HARD, amountToRevoke)).thenReturn(movement);

            Void result = useCase.execute(input);

            assertNull(result);

            verify(transactionRepository, times(1)).save(transactionCaptor.capture());
            Transaction capturedTransaction = transactionCaptor.getValue();

            assertEquals(targetUserId, capturedTransaction.userId());
            assertEquals(CoinType.HARD, capturedTransaction.coinType());
            assertEquals(amountToRevoke, capturedTransaction.amount());
            assertEquals(50, capturedTransaction.balanceBefore());
            assertEquals(30, capturedTransaction.balanceAfter());
            assertEquals(OperationType.DEBIT, capturedTransaction.operation());
            assertEquals(TransactionReason.ADMIN_REVOKE, capturedTransaction.reason());
            assertEquals(adminAuthId, capturedTransaction.referenceId());
        }
    }

    @Nested
    @DisplayName("Autorização e Validação de Permissões")
    class AuthorizationAndPermissions {

        @Test
        @DisplayName("Deve lançar UserIsNotAdminException quando o principal não for um administrador")
        void execute_WhenUserIsNotAdmin_ShouldThrowUserIsNotAdminExceptionAndNotQueryRepositories() {
            RevokeUserWalletInput input = new RevokeUserWalletInput(principal, targetUserId, CoinType.SOFT, 100);

            doThrow(new UserIsNotAdminException())
                    .when(adminChecker).check(principal, PermissionKey.USER, PermissionAction.EDIT);

            assertThrows(
                    UserIsNotAdminException.class,
                    () -> useCase.execute(input)
            );

            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.EDIT);
            verifyNoInteractions(userRepository, transactionRepository);
        }

        @Test
        @DisplayName("Deve lançar PermissionDeniedException quando o admin não possuir permissão EDIT em USER")
        void execute_WhenAdminLacksEditPermission_ShouldThrowPermissionDeniedExceptionAndNotQueryRepositories() {
            RevokeUserWalletInput input = new RevokeUserWalletInput(principal, targetUserId, CoinType.SOFT, 100);

            doThrow(new PermissionDeniedException())
                    .when(adminChecker).check(principal, PermissionKey.USER, PermissionAction.EDIT);

            assertThrows(
                    PermissionDeniedException.class,
                    () -> useCase.execute(input)
            );

            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.EDIT);
            verifyNoInteractions(userRepository, transactionRepository);
        }
    }

    @Nested
    @DisplayName("Exceções de Domínio e Carteira")
    class DomainExceptions {

        @Test
        @DisplayName("Deve lançar UserNotFoundException quando o usuário destino não for encontrado")
        void execute_WhenUserNotFound_ShouldThrowUserNotFoundException() {
            RevokeUserWalletInput input = new RevokeUserWalletInput(principal, targetUserId, CoinType.SOFT, 100);

            when(userRepository.find(targetUserId)).thenReturn(Optional.empty());

            assertThrows(
                    UserNotFoundException.class,
                    () -> useCase.execute(input)
            );

            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.EDIT);
            verify(userRepository, times(1)).find(targetUserId);
            verify(userRepository, never()).save(any());
            verifyNoInteractions(transactionRepository);
        }

        @Test
        @DisplayName("Deve propagar InsufficientBalanceException quando o usuário não possuir saldo suficiente")
        void execute_WhenInsufficientBalance_ShouldPropagateExceptionAndNotSaveEntities() {
            RevokeUserWalletInput input = new RevokeUserWalletInput(principal, targetUserId, CoinType.SOFT, 1000);

            when(userRepository.find(targetUserId)).thenReturn(Optional.of(mockUser));
            when(mockUser.getWallet()).thenReturn(mockWallet);
            doThrow(new InsufficientBalanceException())
                    .when(mockWallet).remove(CoinType.SOFT, 1000);

            assertThrows(
                    InsufficientBalanceException.class,
                    () -> useCase.execute(input)
            );

            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.EDIT);
            verify(userRepository, times(1)).find(targetUserId);
            verify(mockWallet, times(1)).remove(CoinType.SOFT, 1000);
            verify(userRepository, never()).save(any());
            verifyNoInteractions(transactionRepository);
        }

        @Test
        @DisplayName("Deve propagar InvalidPaymentException quando o tipo de moeda fornecido for inválido ou nulo")
        void execute_WhenInvalidCoinType_ShouldPropagateInvalidPaymentExceptionAndNotSaveEntities() {
            RevokeUserWalletInput input = new RevokeUserWalletInput(principal, targetUserId, null, 100);

            when(userRepository.find(targetUserId)).thenReturn(Optional.of(mockUser));
            when(mockUser.getWallet()).thenReturn(mockWallet);
            doThrow(new InvalidPaymentException())
                    .when(mockWallet).remove(null, 100);

            assertThrows(
                    InvalidPaymentException.class,
                    () -> useCase.execute(input)
            );

            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.EDIT);
            verify(userRepository, times(1)).find(targetUserId);
            verify(mockWallet, times(1)).remove(null, 100);
            verify(userRepository, never()).save(any());
            verifyNoInteractions(transactionRepository);
        }
    }

    @Nested
    @DisplayName("Falhas na Camada de Repositório")
    class RepositoryFailures {

        @Test
        @DisplayName("Deve propagar exceção caso o UserRepository falhe ao salvar o usuário e não salvar a transação")
        void execute_WhenUserRepositorySaveFails_ShouldPropagateExceptionAndNotSaveTransaction() {
            int amountToRevoke = 50;
            RevokeUserWalletInput input = new RevokeUserWalletInput(principal, targetUserId, CoinType.SOFT, amountToRevoke);

            Balance balanceBefore = new Balance(200, 10);
            Balance balanceAfter = new Balance(150, 10);
            WalletMovement movement = new WalletMovement(CoinType.SOFT, balanceBefore, balanceAfter, amountToRevoke, OperationType.DEBIT);

            when(userRepository.find(targetUserId)).thenReturn(Optional.of(mockUser));
            when(mockUser.getWallet()).thenReturn(mockWallet);
            when(mockWallet.remove(CoinType.SOFT, amountToRevoke)).thenReturn(movement);

            doThrow(new RuntimeException("Falha ao salvar usuário no banco de dados"))
                    .when(userRepository).save(mockUser);

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> useCase.execute(input)
            );

            assertEquals("Falha ao salvar usuário no banco de dados", exception.getMessage());
            verify(userRepository, times(1)).save(mockUser);
            verifyNoInteractions(transactionRepository);
        }

        @Test
        @DisplayName("Deve propagar exceção caso o TransactionRepository falhe ao salvar a transação")
        void execute_WhenTransactionRepositorySaveFails_ShouldPropagateException() {
            int amountToRevoke = 50;
            RevokeUserWalletInput input = new RevokeUserWalletInput(principal, targetUserId, CoinType.SOFT, amountToRevoke);

            Balance balanceBefore = new Balance(200, 10);
            Balance balanceAfter = new Balance(150, 10);
            WalletMovement movement = new WalletMovement(CoinType.SOFT, balanceBefore, balanceAfter, amountToRevoke, OperationType.DEBIT);

            when(userRepository.find(targetUserId)).thenReturn(Optional.of(mockUser));
            when(mockUser.getWallet()).thenReturn(mockWallet);
            when(mockWallet.remove(CoinType.SOFT, amountToRevoke)).thenReturn(movement);

            doThrow(new RuntimeException("Falha ao salvar transação no banco de dados"))
                    .when(transactionRepository).save(any(Transaction.class));

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> useCase.execute(input)
            );

            assertEquals("Falha ao salvar transação no banco de dados", exception.getMessage());
            verify(userRepository, times(1)).save(mockUser);
            verify(transactionRepository, times(1)).save(any(Transaction.class));
        }
    }
}