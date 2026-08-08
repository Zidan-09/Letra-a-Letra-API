package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.admin.domain.exception.PermissionDeniedException;
import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.user.application.input.GetUserInventoryInput;
import com.letraaletra.api.features.user.application.output.GetUserInventoryOutput;
import com.letraaletra.api.features.user.domain.UsersPage;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import com.letraaletra.api.features.user.domain.repository.inventory.InventoryRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserInventoryUseCase Unit Tests")
class GetUserInventoryUseCaseTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private GetUserInventoryUseCase useCase;

    private AuthenticatedUser principal;
    private UUID targetUserId;
    private Page<InventoryItem> mockPage;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "Admin", true, false);
        targetUserId = UUID.randomUUID();
        mockPage = mock(Page.class);
    }

    @Nested
    @DisplayName("Sucesso no Fluxo Principal")
    class SuccessFlows {

        @Test
        @DisplayName("Deve verificar permissões do admin, construir UsersPage via factory estática e retornar itens paginados")
        void execute_WhenAdminHasPermission_ShouldReturnOutputWithInventoryPage() {
            int page = 0;
            int size = 10;
            Sort sort = Sort.by(Sort.Direction.ASC, "name");
            GetUserInventoryInput input = new GetUserInventoryInput(principal, targetUserId, page, size, sort);

            UsersPage mockUsersPage = mock(UsersPage.class);

            try (MockedStatic<UsersPage> usersPageStaticMock = mockStatic(UsersPage.class)) {
                usersPageStaticMock.when(() -> UsersPage.create(page, size, sort)).thenReturn(mockUsersPage);
                when(inventoryRepository.getUsersCosmeticsPage(targetUserId, mockUsersPage)).thenReturn(mockPage);

                GetUserInventoryOutput output = useCase.execute(input);

                assertNotNull(output);
                assertEquals(mockPage, output.inventory());

                verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.VIEW);
                verify(inventoryRepository, times(1)).getUsersCosmeticsPage(targetUserId, mockUsersPage);
                usersPageStaticMock.verify(() -> UsersPage.create(page, size, sort), times(1));
            }
        }
    }

    @Nested
    @DisplayName("Autorização e Validação de Permissões")
    class AuthorizationAndPermissions {

        @Test
        @DisplayName("Deve interromper a execução e lançar AccessDeniedException quando AdminChecker recusar o acesso")
        void execute_WhenAdminCheckerFails_ShouldThrowAccessDeniedExceptionAndNotQueryRepository() {
            GetUserInventoryInput input = new GetUserInventoryInput(principal, targetUserId, 0, 10, Sort.unsorted());

            doThrow(new PermissionDeniedException())
                    .when(adminChecker).check(principal, PermissionKey.USER, PermissionAction.VIEW);

            PermissionDeniedException exception = assertThrows(
                    PermissionDeniedException.class,
                    () -> useCase.execute(input)
            );

            assertEquals("permission was denied", exception.getMessage());
            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.VIEW);
            verifyNoInteractions(inventoryRepository);
        }
    }

    @Nested
    @DisplayName("Falhas e Exceções do Repositório")
    class RepositoryFailures {

        @Test
        @DisplayName("Deve propagar exceção quando o repositório falhar na consulta paginada")
        void execute_WhenRepositoryThrowsException_ShouldPropagateException() {
            GetUserInventoryInput input = new GetUserInventoryInput(principal, targetUserId, 0, 10, Sort.unsorted());

            when(inventoryRepository.getUsersCosmeticsPage(eq(targetUserId), any(UsersPage.class)))
                    .thenThrow(new RuntimeException("Erro de banco de dados"));

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> useCase.execute(input)
            );

            assertEquals("Erro de banco de dados", exception.getMessage());
            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.VIEW);
            verify(inventoryRepository, times(1)).getUsersCosmeticsPage(eq(targetUserId), any(UsersPage.class));
        }
    }

    @Nested
    @DisplayName("Casos de Borda e Entradas Nulas")
    class NullAndEdgeCases {

        @Test
        @DisplayName("Deve repassar principal nulo para o AdminChecker caso venha nulo no input")
        void execute_WhenPrincipalIsNull_ShouldPassNullToAdminChecker() {
            GetUserInventoryInput input = new GetUserInventoryInput(null, targetUserId, 0, 10, Sort.unsorted());

            when(inventoryRepository.getUsersCosmeticsPage(eq(targetUserId), any(UsersPage.class)))
                    .thenReturn(mockPage);

            GetUserInventoryOutput output = useCase.execute(input);

            assertNotNull(output);
            verify(adminChecker, times(1)).check(null, PermissionKey.USER, PermissionAction.VIEW);
            verify(inventoryRepository, times(1)).getUsersCosmeticsPage(eq(targetUserId), any(UsersPage.class));
        }

        @Test
        @DisplayName("Deve repassar userId nulo para o repositório caso venha nulo no input")
        void execute_WhenUserIdIsNull_ShouldQueryRepositoryWithNullUserId() {
            GetUserInventoryInput input = new GetUserInventoryInput(principal, null, 0, 10, Sort.unsorted());

            when(inventoryRepository.getUsersCosmeticsPage(eq(null), any(UsersPage.class)))
                    .thenReturn(mockPage);

            GetUserInventoryOutput output = useCase.execute(input);

            assertNotNull(output);
            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.VIEW);
            verify(inventoryRepository, times(1)).getUsersCosmeticsPage(eq(null), any(UsersPage.class));
        }

        @Test
        @DisplayName("Deve aceitar Sort nulo no input e repassar para a factory UsersPage.create")
        void execute_WhenSortIsNull_ShouldPassNullSortToUsersPage() {
            GetUserInventoryInput input = new GetUserInventoryInput(principal, targetUserId, 1, 20, null);

            UsersPage mockUsersPage = mock(UsersPage.class);

            try (MockedStatic<UsersPage> usersPageStaticMock = mockStatic(UsersPage.class)) {
                usersPageStaticMock.when(() -> UsersPage.create(1, 20, null)).thenReturn(mockUsersPage);
                when(inventoryRepository.getUsersCosmeticsPage(targetUserId, mockUsersPage)).thenReturn(mockPage);

                GetUserInventoryOutput output = useCase.execute(input);

                assertNotNull(output);
                usersPageStaticMock.verify(() -> UsersPage.create(1, 20, null), times(1));
            }
        }
    }
}