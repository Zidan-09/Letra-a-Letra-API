package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.admin.domain.exception.PermissionDeniedException;
import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.user.application.input.RevokeUserCosmeticInput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.InvalidUserCosmeticSelectedException;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.inventory.Inventory;
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
@DisplayName("RevokeUserCosmeticUseCase Unit Tests")
class RevokeUserCosmeticUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private RevokeUserCosmeticUseCase useCase;

    private AuthenticatedUser principal;
    private UUID targetUserId;
    private UUID cosmeticId;
    private RevokeUserCosmeticInput input;
    private User mockUser;
    private Inventory mockInventory;

    @BeforeEach
    void setUp() {
        UUID adminAuthId = UUID.randomUUID();
        principal = new AuthenticatedUser(adminAuthId, "AdminUser", true, false);
        targetUserId = UUID.randomUUID();
        cosmeticId = UUID.randomUUID();

        input = new RevokeUserCosmeticInput(principal, targetUserId, cosmeticId);
        mockUser = mock(User.class);
        mockInventory = mock(Inventory.class);
    }

    @Nested
    @DisplayName("Sucesso no Fluxo Principal")
    class SuccessFlows {

        @Test
        @DisplayName("Deve verificar permissão, remover o cosmético do inventário e salvar as alterações do usuário")
        void execute_WhenAdminHasPermissionAndUserHasCosmetic_ShouldRevokeAndSaveUser() {
            when(userRepository.find(targetUserId)).thenReturn(Optional.of(mockUser));
            when(mockUser.getInventory()).thenReturn(mockInventory);

            Void result = useCase.execute(input);

            assertNull(result);

            InOrder inOrder = inOrder(adminChecker, userRepository, mockUser, mockInventory);
            inOrder.verify(adminChecker).check(principal, PermissionKey.USER, PermissionAction.EDIT);
            inOrder.verify(userRepository).find(targetUserId);
            inOrder.verify(mockUser).getInventory();
            inOrder.verify(mockInventory).removeFromInventory(cosmeticId);
            inOrder.verify(userRepository).save(mockUser);
        }
    }

    @Nested
    @DisplayName("Autorização e Validação de Permissões")
    class AuthorizationAndPermissions {

        @Test
        @DisplayName("Deve lançar UserIsNotAdminException quando o principal não for um administrador")
        void execute_WhenUserIsNotAdmin_ShouldThrowUserIsNotAdminExceptionAndNotQueryRepository() {
            doThrow(new UserIsNotAdminException())
                    .when(adminChecker).check(principal, PermissionKey.USER, PermissionAction.EDIT);

            assertThrows(
                    UserIsNotAdminException.class,
                    () -> useCase.execute(input)
            );

            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.EDIT);
            verifyNoInteractions(userRepository);
        }

        @Test
        @DisplayName("Deve lançar PermissionDeniedException quando o admin não possuir a permissão EDIT em USER")
        void execute_WhenAdminLacksEditPermission_ShouldThrowPermissionDeniedExceptionAndNotQueryRepository() {
            doThrow(new PermissionDeniedException())
                    .when(adminChecker).check(principal, PermissionKey.USER, PermissionAction.EDIT);

            assertThrows(
                    PermissionDeniedException.class,
                    () -> useCase.execute(input)
            );

            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.EDIT);
            verifyNoInteractions(userRepository);
        }
    }

    @Nested
    @DisplayName("Exceções de Domínio e Inventário")
    class DomainExceptions {

        @Test
        @DisplayName("Deve lançar UserNotFoundException quando o usuário destino não for encontrado no repositório")
        void execute_WhenUserNotFound_ShouldThrowUserNotFoundException() {
            when(userRepository.find(targetUserId)).thenReturn(Optional.empty());

            assertThrows(
                    UserNotFoundException.class,
                    () -> useCase.execute(input)
            );

            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.EDIT);
            verify(userRepository, times(1)).find(targetUserId);
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve propagar InvalidUserCosmeticSelectedException quando o cosmético não pertencer ao inventário do usuário")
        void execute_WhenCosmeticNotInInventory_ShouldPropagateInvalidUserCosmeticSelectedExceptionAndNotSave() {
            when(userRepository.find(targetUserId)).thenReturn(Optional.of(mockUser));
            when(mockUser.getInventory()).thenReturn(mockInventory);
            doThrow(new InvalidUserCosmeticSelectedException())
                    .when(mockInventory).removeFromInventory(cosmeticId);

            assertThrows(
                    InvalidUserCosmeticSelectedException.class,
                    () -> useCase.execute(input)
            );

            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.EDIT);
            verify(userRepository, times(1)).find(targetUserId);
            verify(mockInventory, times(1)).removeFromInventory(cosmeticId);
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Falhas na Camada de Repositório")
    class RepositoryFailures {

        @Test
        @DisplayName("Deve propagar exceção caso o UserRepository falhe ao salvar as alterações do usuário")
        void execute_WhenUserRepositorySaveFails_ShouldPropagateException() {
            when(userRepository.find(targetUserId)).thenReturn(Optional.of(mockUser));
            when(mockUser.getInventory()).thenReturn(mockInventory);
            doThrow(new RuntimeException("Erro ao conectar ao banco de dados"))
                    .when(userRepository).save(mockUser);

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> useCase.execute(input)
            );

            assertEquals("Erro ao conectar ao banco de dados", exception.getMessage());
            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.EDIT);
            verify(userRepository, times(1)).find(targetUserId);
            verify(mockInventory, times(1)).removeFromInventory(cosmeticId);
            verify(userRepository, times(1)).save(mockUser);
        }
    }
}