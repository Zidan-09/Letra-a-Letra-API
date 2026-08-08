package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.admin.domain.exception.PermissionDeniedException;
import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.user.application.input.UnbanUserInput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.ban.BanHistory;
import com.letraaletra.api.features.user.domain.exception.UserDoesNotHaveBanException;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.banhistory.BanHistoryRepository;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.domain.security.exceptions.UserIsNotAdminException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
@DisplayName("UnbanUserUseCase Unit Tests")
class UnbanUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BanHistoryRepository banHistoryRepository;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private UnbanUserUseCase useCase;

    private AuthenticatedUser principal;
    private UUID adminAuthId;
    private UUID targetUserId;
    private UnbanUserInput input;
    private User mockUser;
    private BanHistory mockBanHistory;

    @BeforeEach
    void setUp() {
        adminAuthId = UUID.randomUUID();
        principal = new AuthenticatedUser(adminAuthId, "AdminUser", true, false);
        targetUserId = UUID.randomUUID();

        input = new UnbanUserInput(principal, targetUserId);
        mockUser = mock(User.class);
        mockBanHistory = mock(BanHistory.class);
    }

    @Nested
    @DisplayName("sucesso no Fluxo Principal")
    class SuccessFlows {

        @Test
        @DisplayName("Deve revogar o banimento do usuário, atualizar o histórico e salvar ambas as entidades")
        void execute_WhenUserIsBannedAndHasActiveBanHistory_ShouldUnbanAndSaveEntities() {
            when(userRepository.find(targetUserId)).thenReturn(Optional.of(mockUser));
            when(mockUser.isBanned()).thenReturn(true);
            when(banHistoryRepository.findActiveByUserId(targetUserId)).thenReturn(Optional.of(mockBanHistory));

            Void result = useCase.execute(input);

            assertNull(result);

            InOrder inOrder = inOrder(adminChecker, userRepository, mockUser, banHistoryRepository, mockBanHistory);
            inOrder.verify(adminChecker).check(principal, PermissionKey.USER, PermissionAction.EDIT);
            inOrder.verify(userRepository).find(targetUserId);
            inOrder.verify(mockUser).isBanned();
            inOrder.verify(banHistoryRepository).findActiveByUserId(targetUserId);
            inOrder.verify(mockBanHistory).removeBan(adminAuthId);
            inOrder.verify(mockUser).unban();
            inOrder.verify(banHistoryRepository).save(mockBanHistory);
            inOrder.verify(userRepository).save(mockUser);
        }
    }

    @Nested
    @DisplayName("Autorização e Validação de Permissões")
    class AuthorizationAndPermissions {

        @Test
        @DisplayName("Deve lançar UserIsNotAdminException quando o principal não for um administrador")
        void execute_WhenUserIsNotAdmin_ShouldThrowUserIsNotAdminExceptionAndNotQueryRepositories() {
            doThrow(new UserIsNotAdminException())
                    .when(adminChecker).check(principal, PermissionKey.USER, PermissionAction.EDIT);

            assertThrows(
                    UserIsNotAdminException.class,
                    () -> useCase.execute(input)
            );

            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.EDIT);
            verifyNoInteractions(userRepository, banHistoryRepository);
        }

        @Test
        @DisplayName("Deve lançar PermissionDeniedException quando o admin não possuir a permissão EDIT em USER")
        void execute_WhenAdminLacksEditPermission_ShouldThrowPermissionDeniedExceptionAndNotQueryRepositories() {
            doThrow(new PermissionDeniedException())
                    .when(adminChecker).check(principal, PermissionKey.USER, PermissionAction.EDIT);

            assertThrows(
                    PermissionDeniedException.class,
                    () -> useCase.execute(input)
            );

            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.EDIT);
            verifyNoInteractions(userRepository, banHistoryRepository);
        }
    }

    @Nested
    @DisplayName("Exceções de Domínio e Banimento")
    class DomainExceptions {

        @Test
        @DisplayName("Deve lançar UserNotFoundException quando o usuário destino não for encontrado")
        void execute_WhenUserNotFound_ShouldThrowUserNotFoundException() {
            when(userRepository.find(targetUserId)).thenReturn(Optional.empty());

            assertThrows(
                    UserNotFoundException.class,
                    () -> useCase.execute(input)
            );

            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.EDIT);
            verify(userRepository, times(1)).find(targetUserId);
            verifyNoInteractions(banHistoryRepository);
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar UserDoesNotHaveBanException quando o usuário não estiver banido")
        void execute_WhenUserIsNotBanned_ShouldThrowUserDoesNotHaveBanExceptionAndNotQueryBanHistory() {
            when(userRepository.find(targetUserId)).thenReturn(Optional.of(mockUser));
            when(mockUser.isBanned()).thenReturn(false);

            assertThrows(
                    UserDoesNotHaveBanException.class,
                    () -> useCase.execute(input)
            );

            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.EDIT);
            verify(userRepository, times(1)).find(targetUserId);
            verify(mockUser, times(1)).isBanned();
            verifyNoInteractions(banHistoryRepository);
            verify(mockUser, never()).unban();
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar UserDoesNotHaveBanException quando o histórico de banimento ativo não for encontrado")
        void execute_WhenActiveBanHistoryNotFound_ShouldThrowUserDoesNotHaveBanException() {
            when(userRepository.find(targetUserId)).thenReturn(Optional.of(mockUser));
            when(mockUser.isBanned()).thenReturn(true);
            when(banHistoryRepository.findActiveByUserId(targetUserId)).thenReturn(Optional.empty());

            assertThrows(
                    UserDoesNotHaveBanException.class,
                    () -> useCase.execute(input)
            );

            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.EDIT);
            verify(userRepository, times(1)).find(targetUserId);
            verify(mockUser, times(1)).isBanned();
            verify(banHistoryRepository, times(1)).findActiveByUserId(targetUserId);
            verify(mockUser, never()).unban();
            verify(banHistoryRepository, never()).save(any());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve propagar UserDoesNotHaveBanException quando a execução de unban no domínio falhar")
        void execute_WhenUserUnbanFailsInDomain_ShouldPropagateExceptionAndNotSaveEntities() {
            when(userRepository.find(targetUserId)).thenReturn(Optional.of(mockUser));
            when(mockUser.isBanned()).thenReturn(true);
            when(banHistoryRepository.findActiveByUserId(targetUserId)).thenReturn(Optional.of(mockBanHistory));
            doThrow(new UserDoesNotHaveBanException()).when(mockUser).unban();

            assertThrows(
                    UserDoesNotHaveBanException.class,
                    () -> useCase.execute(input)
            );

            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.EDIT);
            verify(userRepository, times(1)).find(targetUserId);
            verify(banHistoryRepository, times(1)).findActiveByUserId(targetUserId);
            verify(mockBanHistory, times(1)).removeBan(adminAuthId);
            verify(mockUser, times(1)).unban();
            verify(banHistoryRepository, never()).save(any());
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Falhas na Camada de Repositório")
    class RepositoryFailures {

        @Test
        @DisplayName("Deve propagar exceção caso o BanHistoryRepository falhe ao salvar o histórico e não salvar o usuário")
        void execute_WhenBanHistoryRepositorySaveFails_ShouldPropagateExceptionAndNotSaveUser() {
            when(userRepository.find(targetUserId)).thenReturn(Optional.of(mockUser));
            when(mockUser.isBanned()).thenReturn(true);
            when(banHistoryRepository.findActiveByUserId(targetUserId)).thenReturn(Optional.of(mockBanHistory));

            doThrow(new RuntimeException("Erro ao salvar histórico de banimento"))
                    .when(banHistoryRepository).save(mockBanHistory);

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> useCase.execute(input)
            );

            assertEquals("Erro ao salvar histórico de banimento", exception.getMessage());
            verify(banHistoryRepository, times(1)).save(mockBanHistory);
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve propagar exceção caso o UserRepository falhe ao salvar o usuário")
        void execute_WhenUserRepositorySaveFails_ShouldPropagateException() {
            when(userRepository.find(targetUserId)).thenReturn(Optional.of(mockUser));
            when(mockUser.isBanned()).thenReturn(true);
            when(banHistoryRepository.findActiveByUserId(targetUserId)).thenReturn(Optional.of(mockBanHistory));

            doThrow(new RuntimeException("Erro ao salvar usuário no banco de dados"))
                    .when(userRepository).save(mockUser);

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> useCase.execute(input)
            );

            assertEquals("Erro ao salvar usuário no banco de dados", exception.getMessage());
            verify(banHistoryRepository, times(1)).save(mockBanHistory);
            verify(userRepository, times(1)).save(mockUser);
        }
    }
}