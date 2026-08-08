package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.admin.domain.exception.PermissionDeniedException;
import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.user.application.input.GetUsersInput;
import com.letraaletra.api.features.user.application.output.GetUsersOutput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.UsersPage;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUsersUseCase Unit Tests")
class GetUsersUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private GetUsersUseCase useCase;

    @Captor
    private ArgumentCaptor<UsersPage> usersPageCaptor;

    private AuthenticatedUser principal;
    private Page<User> mockPage;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "AdminUser", true, false);
        mockPage = mock(Page.class);
    }

    @Nested
    @DisplayName("sucesso no Fluxo Principal")
    class SuccessFlows {

        @Test
        @DisplayName("Deve verificar permissão de admin, instanciar UsersPage e retornar lista paginada de usuários")
        void execute_WhenAdminHasPermission_ShouldReturnOutputWithUsersPage() {
            int page = 0;
            int size = 15;
            Sort sort = Sort.by(Sort.Direction.ASC, "username");
            GetUsersInput input = new GetUsersInput(principal, page, size, sort);

            when(userRepository.get(any(UsersPage.class))).thenReturn(mockPage);

            GetUsersOutput output = useCase.execute(input);

            assertNotNull(output);
            assertEquals(mockPage, output.users());

            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.VIEW);
            verify(userRepository, times(1)).get(usersPageCaptor.capture());

            UsersPage capturedPage = usersPageCaptor.getValue();
            assertNotNull(capturedPage);
        }
    }

    @Nested
    @DisplayName("Autorização e Validação de Permissões")
    class AuthorizationAndPermissions {

        @Test
        @DisplayName("Deve lançar UserIsNotAdminException quando o usuário não for administrador")
        void execute_WhenUserIsNotAdmin_ShouldThrowUserIsNotAdminExceptionAndNotQueryRepository() {
            GetUsersInput input = new GetUsersInput(principal, 0, 10, Sort.unsorted());

            doThrow(new UserIsNotAdminException())
                    .when(adminChecker).check(principal, PermissionKey.USER, PermissionAction.VIEW);

            assertThrows(
                    UserIsNotAdminException.class,
                    () -> useCase.execute(input)
            );

            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.VIEW);
            verifyNoInteractions(userRepository);
        }

        @Test
        @DisplayName("Deve lançar PermissionDeniedException quando o admin não possuir a permissão requerida")
        void execute_WhenAdminLacksPermission_ShouldThrowPermissionDeniedExceptionAndNotQueryRepository() {
            GetUsersInput input = new GetUsersInput(principal, 0, 10, Sort.unsorted());

            doThrow(new PermissionDeniedException())
                    .when(adminChecker).check(principal, PermissionKey.USER, PermissionAction.VIEW);

            assertThrows(
                    PermissionDeniedException.class,
                    () -> useCase.execute(input)
            );

            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.VIEW);
            verifyNoInteractions(userRepository);
        }
    }

    @Nested
    @DisplayName("Falhas e Exceções do Repositório")
    class RepositoryFailures {

        @Test
        @DisplayName("Deve propagar exceção quando o repositório falhar ao buscar a página de usuários")
        void execute_WhenRepositoryThrowsException_ShouldPropagateException() {
            GetUsersInput input = new GetUsersInput(principal, 0, 10, Sort.unsorted());

            when(userRepository.get(any(UsersPage.class)))
                    .thenThrow(new RuntimeException("Erro ao realizar consulta no banco de dados"));

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> useCase.execute(input)
            );

            assertEquals("Erro ao realizar consulta no banco de dados", exception.getMessage());
            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.VIEW);
            verify(userRepository, times(1)).get(any(UsersPage.class));
        }
    }

    @Nested
    @DisplayName("Casos de Borda e Entradas Nulas")
    class NullAndEdgeCases {

        @Test
        @DisplayName("Deve repassar principal nulo para o AdminChecker caso venha nulo no input")
        void execute_WhenPrincipalIsNull_ShouldPassNullToAdminChecker() {
            GetUsersInput input = new GetUsersInput(null, 0, 10, Sort.unsorted());

            when(userRepository.get(any(UsersPage.class))).thenReturn(mockPage);

            GetUsersOutput output = useCase.execute(input);

            assertNotNull(output);
            verify(adminChecker, times(1)).check(null, PermissionKey.USER, PermissionAction.VIEW);
            verify(userRepository, times(1)).get(any(UsersPage.class));
        }

        @Test
        @DisplayName("Deve aceitar Sort nulo no input e repassar para a instância de UsersPage")
        void execute_WhenSortIsNull_ShouldPassNullSortToUsersPage() {
            GetUsersInput input = new GetUsersInput(principal, 1, 20, null);

            when(userRepository.get(any(UsersPage.class))).thenReturn(mockPage);

            GetUsersOutput output = useCase.execute(input);

            assertNotNull(output);
            verify(adminChecker, times(1)).check(principal, PermissionKey.USER, PermissionAction.VIEW);
            verify(userRepository, times(1)).get(usersPageCaptor.capture());
        }
    }
}