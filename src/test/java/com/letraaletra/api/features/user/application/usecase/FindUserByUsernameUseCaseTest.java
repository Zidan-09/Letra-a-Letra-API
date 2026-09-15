package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.FindUserByUsernameInput;
import com.letraaletra.api.features.user.application.output.FindUserByUsernameOutput;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindUserByUsernameUseCase Unit Tests")
class FindUserByUsernameUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private com.letraaletra.api.features.user.application.port.UserEquippedItemsProvider equippedItemsProvider;

    @InjectMocks
    private FindUserByUsernameUseCase useCase;

    @Captor
    private ArgumentCaptor<UsersPage> pageCaptor;

    private AuthenticatedUser principal;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "Usuario Teste", false, false);
        org.mockito.Mockito.lenient().when(equippedItemsProvider.equippedFor(any())).thenReturn(java.util.Map.of());
    }

    @Nested
    @DisplayName("Sucesso no Fluxo Principal")
    class SuccessFlows {

        @Test
        @SuppressWarnings("unchecked")
        @DisplayName("Deve delegar para search e retornar página quando username for encontrado")
        void execute_WhenUserExists_ShouldReturnPageWithUser() {
            String username = "joaosilva";
            FindUserByUsernameInput input = new FindUserByUsernameInput(principal, username, 0, 20, Sort.unsorted());
            Page<User> mockPage = mock(Page.class);
            org.mockito.Mockito.lenient().when(mockPage.getContent()).thenReturn(java.util.List.of());

            when(userRepository.search(eq(username), any(UsersPage.class))).thenReturn(mockPage);

            FindUserByUsernameOutput output = useCase.execute(input);

            assertNotNull(output);
            assertEquals(mockPage, output.users());

            verify(userRepository, times(1)).search(eq(username), pageCaptor.capture());
            UsersPage captured = pageCaptor.getValue();
            assertEquals(0, captured.page());
            assertEquals(20, captured.size());
            assertEquals(Sort.unsorted(), captured.sort());
        }

        @Test
        @SuppressWarnings("unchecked")
        @DisplayName("Deve mapear page/size/sort corretamente para UsersPage")
        void execute_WithPagination_ShouldMapToUsersPage() {
            String username = "casa";
            Sort sort = Sort.by(Sort.Direction.ASC, "username");
            FindUserByUsernameInput input = new FindUserByUsernameInput(principal, username, 1, 10, sort);
            Page<User> mockPage = mock(Page.class);
            org.mockito.Mockito.lenient().when(mockPage.getContent()).thenReturn(java.util.List.of());

            when(userRepository.search(eq(username), any(UsersPage.class))).thenReturn(mockPage);

            FindUserByUsernameOutput output = useCase.execute(input);

            assertNotNull(output);
            verify(userRepository, times(1)).search(eq(username), pageCaptor.capture());
            UsersPage captured = pageCaptor.getValue();
            assertEquals(1, captured.page());
            assertEquals(10, captured.size());
            assertEquals(sort, captured.sort());
        }

        @Test
        @SuppressWarnings("unchecked")
        @DisplayName("Não deve lançar exceção quando nenhum usuário semelhante for encontrado, apenas retornar página vazia")
        void execute_WhenNoSimilarUser_ShouldReturnEmptyPageInsteadOfThrowing() {
            String username = "casa";
            FindUserByUsernameInput input = new FindUserByUsernameInput(principal, username, 0, 20, Sort.unsorted());
            Page<User> emptyPage = mock(Page.class);

            when(emptyPage.getContent()).thenReturn(List.of());
            when(userRepository.search(eq(username), any(UsersPage.class))).thenReturn(emptyPage);

            FindUserByUsernameOutput output = useCase.execute(input);

            assertNotNull(output);
            assertTrue(output.users().getContent().isEmpty());
            verify(userRepository, times(1)).search(eq(username), any(UsersPage.class));
        }
    }

    @Nested
    @DisplayName("Busca e Exceções")
    class UserLookupFailures {

        @Test
        @DisplayName("Deve propagar exceção quando o repositório lançar um erro inesperado")
        void execute_WhenRepositoryThrowsException_ShouldPropagateException() {
            String username = "erro_banco";
            FindUserByUsernameInput input = new FindUserByUsernameInput(principal, username, 0, 20, Sort.unsorted());

            when(userRepository.search(eq(username), any(UsersPage.class)))
                    .thenThrow(new RuntimeException("Falha ao conectar com o banco de dados"));

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> useCase.execute(input)
            );

            assertEquals("Falha ao conectar com o banco de dados", exception.getMessage());
            verify(userRepository, times(1)).search(eq(username), any(UsersPage.class));
        }
    }

    @Nested
    @DisplayName("Casos de Borda e Entradas Nulas ou Vazias")
    class NullAndEdgeCases {

        @Test
        @SuppressWarnings("unchecked")
        @DisplayName("Deve aceitar principal nulo no input e ainda consultar o repositório")
        void execute_WhenPrincipalIsNull_ShouldStillQueryRepository() {
            String username = "joaosilva";
            FindUserByUsernameInput inputWithNullPrincipal = new FindUserByUsernameInput(null, username, 0, 20, Sort.unsorted());
            Page<User> mockPage = mock(Page.class);
            org.mockito.Mockito.lenient().when(mockPage.getContent()).thenReturn(java.util.List.of());

            when(userRepository.search(eq(username), any(UsersPage.class))).thenReturn(mockPage);

            FindUserByUsernameOutput output = useCase.execute(inputWithNullPrincipal);

            assertNotNull(output);
            assertEquals(mockPage, output.users());
            verify(userRepository, times(1)).search(eq(username), any(UsersPage.class));
        }

        @Test
        @SuppressWarnings("unchecked")
        @DisplayName("Deve repassar username parcial para o repositório sem sanitização no usecase")
        void execute_ShouldPassExactUsernameToRepositoryWithoutSanitization() {
            String rawUsername = "  UserTest  ";
            FindUserByUsernameInput input = new FindUserByUsernameInput(principal, rawUsername, 0, 20, Sort.unsorted());
            Page<User> mockPage = mock(Page.class);
            org.mockito.Mockito.lenient().when(mockPage.getContent()).thenReturn(java.util.List.of());

            when(userRepository.search(eq(rawUsername), any(UsersPage.class))).thenReturn(mockPage);

            FindUserByUsernameOutput output = useCase.execute(input);

            assertNotNull(output);
            verify(userRepository, times(1)).search(eq(rawUsername), any(UsersPage.class));
        }
    }
}
