package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.user.application.input.BanUserInput;
import com.letraaletra.api.features.user.domain.BanType;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.ban.BanHistory;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.banhistory.BanHistoryRepository;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BanUserUseCase Unit Tests")
class BanUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BanHistoryRepository banHistoryRepository;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private BanUserUseCase useCase;

    private AuthenticatedUser principal;
    private UUID userId;
    private UUID adminAuthId;
    private BanUserInput input;
    private User user;

    @BeforeEach
    void setUp() {
        adminAuthId = UUID.randomUUID();
        userId = UUID.randomUUID();
        principal = new AuthenticatedUser(adminAuthId, "Admin User", true, false);
        input = new BanUserInput(principal, userId, BanType.TEMPORARY, 60, "Violacao dos termos de servico");
        user = mock(User.class);
    }

    @Nested
    @DisplayName("Sucesso no Fluxo Principal")
    class SuccessFlows {

        @Test
        @DisplayName("Deve banir o usuario com sucesso e salvar o historico e alteracoes do usuario")
        void execute_WhenAllConditionsAreMet_ShouldBanUserAndPersist() {
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(input.expiresIn());
            when(userRepository.find(userId)).thenReturn(Optional.of(user));

            BanHistory banHistoryMock = mock(BanHistory.class);
            when(banHistoryMock.getExpiresAt()).thenReturn(expiresAt);
            when(banHistoryMock.getReason()).thenReturn(input.reason());

            try (MockedStatic<BanHistory> banHistoryStaticMock = mockStatic(BanHistory.class)) {
                banHistoryStaticMock.when(() -> BanHistory.create(
                        eq(userId),
                        eq(adminAuthId),
                        eq(input.reason()),
                        eq(input.type()),
                        eq(input.expiresIn())
                )).thenReturn(banHistoryMock);

                Void result = useCase.execute(input);

                assertNull(result, "O retorno do usecase deve ser null");

                InOrder inOrder = inOrder(adminChecker, userRepository, banHistoryRepository, user);
                inOrder.verify(adminChecker).check(principal, PermissionKey.USER, PermissionAction.EDIT);
                inOrder.verify(userRepository).find(userId);
                inOrder.verify(user).ban(expiresAt, input.reason());
                inOrder.verify(banHistoryRepository).save(banHistoryMock);
                inOrder.verify(userRepository).save(user);

                verifyNoMoreInteractions(userRepository, banHistoryRepository, adminChecker);
            }
        }
    }

    @Nested
    @DisplayName("Validacao de Permissoes e Autorizacao")
    class PermissionChecks {

        @Test
        @DisplayName("Deve interromper a execucao e lancar excecao quando AdminChecker falhar")
        void execute_WhenAdminCheckFails_ShouldThrowExceptionAndNotAccessRepositories() {
            doThrow(new SecurityException("Acesso negado"))
                    .when(adminChecker).check(principal, PermissionKey.USER, PermissionAction.EDIT);

            SecurityException exception = assertThrows(
                    SecurityException.class,
                    () -> useCase.execute(input)
            );

            assertEquals("Acesso negado", exception.getMessage());
            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.EDIT);
            verify(userRepository, never()).find(any());
            verifyNoInteractions(banHistoryRepository);
        }
    }

    @Nested
    @DisplayName("Busca e Existencia de Usuario")
    class UserLookupFailures {

        @Test
        @DisplayName("Deve lancar UserNotFoundException quando usuario nao for encontrado no repositorio")
        void execute_WhenUserNotFound_ShouldThrowUserNotFoundException() {
            when(userRepository.find(userId)).thenReturn(Optional.empty());

            assertThrows(
                    UserNotFoundException.class,
                    () -> useCase.execute(input)
            );

            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.EDIT);
            verify(userRepository, times(1)).find(userId);
            verifyNoInteractions(banHistoryRepository);
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Regras de Dominio e Integridade")
    class DomainLogicAndInvariants {

        @Test
        @DisplayName("Deve interromper o fluxo se a criacao do BanHistory lancar excecao de dominio")
        void execute_WhenBanHistoryCreationFails_ShouldNotBanUserOrPersist() {
            BanUserInput invalidInput = new BanUserInput(principal, userId, BanType.TEMPORARY, -10, "");

            when(userRepository.find(userId)).thenReturn(Optional.of(user));

            try (MockedStatic<BanHistory> banHistoryStaticMock = mockStatic(BanHistory.class)) {
                banHistoryStaticMock.when(() -> BanHistory.create(
                        userId,
                        adminAuthId,
                        invalidInput.reason(),
                        invalidInput.type(),
                        invalidInput.expiresIn()
                )).thenThrow(new IllegalArgumentException("Motivo invalido para banimento"));

                IllegalArgumentException exception = assertThrows(
                        IllegalArgumentException.class,
                        () -> useCase.execute(invalidInput)
                );

                assertEquals("Motivo invalido para banimento", exception.getMessage());
                verify(user, never()).ban(any(), any());
                verifyNoInteractions(banHistoryRepository);
                verify(userRepository, never()).save(any());
            }
        }

        @Test
        @DisplayName("Deve interromper o fluxo se o método user.ban() lancar uma excecao de dominio (Ex: usuario ja banido)")
        void execute_WhenUserBanDomainMethodFails_ShouldNotSaveToRepositories() {
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(input.expiresIn());
            when(userRepository.find(userId)).thenReturn(Optional.of(user));

            BanHistory banHistoryMock = mock(BanHistory.class);
            when(banHistoryMock.getExpiresAt()).thenReturn(expiresAt);
            when(banHistoryMock.getReason()).thenReturn(input.reason());

            doThrow(new RuntimeException("UserAlreadyWasBannedException"))
                    .when(user).ban(expiresAt, input.reason());

            try (MockedStatic<BanHistory> banHistoryStaticMock = mockStatic(BanHistory.class)) {
                banHistoryStaticMock.when(() -> BanHistory.create(
                        userId,
                        adminAuthId,
                        input.reason(),
                        input.type(),
                        input.expiresIn()
                )).thenReturn(banHistoryMock);

                RuntimeException exception = assertThrows(
                        RuntimeException.class,
                        () -> useCase.execute(input)
                );

                assertEquals("UserAlreadyWasBannedException", exception.getMessage());
                verify(banHistoryRepository, never()).save(any());
                verify(userRepository, never()).save(any());
            }
        }
    }

    @Nested
    @DisplayName("Falhas na Camada de Persistencia (Repositórios)")
    class RepositoryFailures {

        @Test
        @DisplayName("Deve interromper o fluxo sem salvar o usuario se o banHistoryRepository falhar ao salvar")
        void execute_WhenBanHistoryRepositoryFails_ShouldNotSaveUser() {
            when(userRepository.find(userId)).thenReturn(Optional.of(user));

            BanHistory banHistoryMock = mock(BanHistory.class);

            doThrow(new RuntimeException("Erro de conexao com o banco de dados"))
                    .when(banHistoryRepository).save(banHistoryMock);

            try (MockedStatic<BanHistory> banHistoryStaticMock = mockStatic(BanHistory.class)) {
                banHistoryStaticMock.when(() -> BanHistory.create(
                        userId,
                        adminAuthId,
                        input.reason(),
                        input.type(),
                        input.expiresIn()
                )).thenReturn(banHistoryMock);

                RuntimeException exception = assertThrows(
                        RuntimeException.class,
                        () -> useCase.execute(input)
                );

                assertEquals("Erro de conexao com o banco de dados", exception.getMessage());
                verify(banHistoryRepository, times(1)).save(banHistoryMock);
                verify(userRepository, never()).save(user);
            }
        }
    }

    @Nested
    @DisplayName("Casos de Borda e Entradas Nulas")
    class NullAndEdgeCases {

        @Test
        @DisplayName("Deve repassar input.principal() nulo para o AdminChecker lançar exceção")
        void execute_WhenPrincipalIsNull_ShouldThrowExceptionFromAdminChecker() {
            BanUserInput nullPrincipalInput = new BanUserInput(null, userId, BanType.TEMPORARY, 30, "Reason");

            doThrow(new NullPointerException("Principal e obrigatorio"))
                    .when(adminChecker).check(null, PermissionKey.USER, PermissionAction.EDIT);

            assertThrows(
                    NullPointerException.class,
                    () -> useCase.execute(nullPrincipalInput)
            );

            verify(adminChecker, times(1)).check(null, PermissionKey.USER, PermissionAction.EDIT);
            verifyNoInteractions(userRepository, banHistoryRepository);
        }
    }

    @Nested
    @DisplayName("Especificação de Comportamento e Contratos Futuros")
    class MissingBehaviorSpecificationTests {

        @Test
        @DisplayName("ESPECIFICACAO: Garante que os parametros corretos do input sao repassados para a fabricacao do BanHistory")
        void execute_ShouldPassExactInputValuesToBanHistoryCreate() {
            when(userRepository.find(userId)).thenReturn(Optional.of(user));

            BanHistory banHistoryMock = mock(BanHistory.class);

            try (MockedStatic<BanHistory> banHistoryStaticMock = mockStatic(BanHistory.class)) {
                banHistoryStaticMock.when(() -> BanHistory.create(
                        userId,
                        adminAuthId,
                        input.reason(),
                        input.type(),
                        input.expiresIn()
                )).thenReturn(banHistoryMock);

                useCase.execute(input);

                banHistoryStaticMock.verify(
                        () -> BanHistory.create(userId, adminAuthId, input.reason(), input.type(), input.expiresIn()),
                        times(1)
                );
            }
        }
    }
}