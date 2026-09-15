package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.GetUsersInput;
import com.letraaletra.api.features.user.application.output.GetUsersOutput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.UsersPage;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUsersUseCase Unit Tests")
class GetUsersUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private com.letraaletra.api.features.user.application.port.UserEquippedItemsProvider equippedItemsProvider;

    @InjectMocks
    private GetUsersUseCase useCase;

    @Captor
    private ArgumentCaptor<UsersPage> usersPageCaptor;

    private AuthenticatedUser adminPrincipal;
    private AuthenticatedUser commonPrincipal;
    private Page<User> mockPage;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        adminPrincipal = new AuthenticatedUser(UUID.randomUUID(), "AdminUser", true, false);
        commonPrincipal = new AuthenticatedUser(UUID.randomUUID(), "CommonUser", false, false);
        mockPage = mock(Page.class);
        org.mockito.Mockito.lenient().when(mockPage.getContent()).thenReturn(java.util.List.of());
        org.mockito.Mockito.lenient().when(equippedItemsProvider.equippedFor(any())).thenReturn(java.util.Map.of());
    }

    @Nested
    @DisplayName("sucesso no Fluxo Principal")
    class SuccessFlows {

        @Test
        @DisplayName("Deve instanciar UsersPage e retornar lista paginada quando admin solicitar")
        void execute_WhenAdminRequests_ShouldReturnOutputWithUsersPage() {
            int page = 0;
            int size = 15;
            Sort sort = Sort.by(Sort.Direction.ASC, "username");
            GetUsersInput input = new GetUsersInput(adminPrincipal, page, size, sort);

            when(userRepository.get(any(UsersPage.class))).thenReturn(mockPage);

            GetUsersOutput output = useCase.execute(input);

            assertNotNull(output);
            assertEquals(mockPage, output.users());

            verify(userRepository, times(1)).get(usersPageCaptor.capture());

            UsersPage capturedPage = usersPageCaptor.getValue();
            assertNotNull(capturedPage);
        }

        @Test
        @DisplayName("Deve retornar lista paginada quando usuário comum autenticado solicitar")
        void execute_WhenCommonUserRequests_ShouldReturnOutputWithUsersPage() {
            int page = 0;
            int size = 15;
            Sort sort = Sort.by(Sort.Direction.ASC, "username");
            GetUsersInput input = new GetUsersInput(commonPrincipal, page, size, sort);

            when(userRepository.get(any(UsersPage.class))).thenReturn(mockPage);

            GetUsersOutput output = useCase.execute(input);

            assertNotNull(output);
            assertEquals(mockPage, output.users());

            verify(userRepository, times(1)).get(usersPageCaptor.capture());

            UsersPage capturedPage = usersPageCaptor.getValue();
            assertNotNull(capturedPage);
        }
    }

    @Nested
    @DisplayName("Falhas e Exceções do Repositório")
    class RepositoryFailures {

        @Test
        @DisplayName("Deve propagar exceção quando o repositório falhar ao buscar a página de usuários")
        void execute_WhenRepositoryThrowsException_ShouldPropagateException() {
            GetUsersInput input = new GetUsersInput(commonPrincipal, 0, 10, Sort.unsorted());

            when(userRepository.get(any(UsersPage.class)))
                    .thenThrow(new RuntimeException("Erro ao realizar consulta no banco de dados"));

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> useCase.execute(input)
            );

            assertEquals("Erro ao realizar consulta no banco de dados", exception.getMessage());
            verify(userRepository, times(1)).get(any(UsersPage.class));
        }
    }

    @Nested
    @DisplayName("Casos de Borda e Entradas Nulas")
    class NullAndEdgeCases {

        @Test
        @DisplayName("Deve aceitar principal nulo no input e ainda consultar o repositório")
        void execute_WhenPrincipalIsNull_ShouldStillQueryRepository() {
            GetUsersInput input = new GetUsersInput(null, 0, 10, Sort.unsorted());

            when(userRepository.get(any(UsersPage.class))).thenReturn(mockPage);

            GetUsersOutput output = useCase.execute(input);

            assertNotNull(output);
            verify(userRepository, times(1)).get(any(UsersPage.class));
        }

        @Test
        @DisplayName("Deve aceitar Sort nulo no input e repassar para a instância de UsersPage")
        void execute_WhenSortIsNull_ShouldPassNullSortToUsersPage() {
            GetUsersInput input = new GetUsersInput(commonPrincipal, 1, 20, null);

            when(userRepository.get(any(UsersPage.class))).thenReturn(mockPage);

            GetUsersOutput output = useCase.execute(input);

            assertNotNull(output);
            verify(userRepository, times(1)).get(usersPageCaptor.capture());
        }
    }
}
