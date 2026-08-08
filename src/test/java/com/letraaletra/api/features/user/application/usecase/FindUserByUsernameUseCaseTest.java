package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.FindUserByUsernameInput;
import com.letraaletra.api.features.user.application.output.FindUserByUsernameOutput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindUserByUsernameUseCase Unit Tests")
class FindUserByUsernameUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FindUserByUsernameUseCase useCase;

    private AuthenticatedUser principal;
    private User user;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "Usuario Teste", false, false);
        user = mock(User.class);
    }

    @Nested
    @DisplayName("Sucesso no Fluxo Principal")
    class SuccessFlows {

        @Test
        @DisplayName("Deve retornar FindUserByUsernameOutput contendo o usuário quando o username for encontrado")
        void execute_WhenUserExists_ShouldReturnOutputWithUser() {
            String username = "joaosilva";
            FindUserByUsernameInput input = new FindUserByUsernameInput(principal, username);

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            FindUserByUsernameOutput output = useCase.execute(input);

            assertNotNull(output);
            assertEquals(user, output.user());

            verify(userRepository, times(1)).findByUsername(username);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "user.name",
                "user_123",
                "A",
                "usuario_com_nome_extremamente_longo_para_testar_limite_de_caracteres"
        })
        @DisplayName("Deve buscar com sucesso para diferentes variações de sintaxe de username válidos")
        void execute_WithVariousValidUsernames_ShouldReturnUser(String username) {
            FindUserByUsernameInput input = new FindUserByUsernameInput(principal, username);

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            FindUserByUsernameOutput output = useCase.execute(input);

            assertNotNull(output);
            assertEquals(user, output.user());

            verify(userRepository, times(1)).findByUsername(username);
        }
    }

    @Nested
    @DisplayName("Busca e Exceções")
    class UserLookupFailures {

        @Test
        @DisplayName("Deve lançar UserNotFoundException quando o repositório retornar Optional.empty()")
        void execute_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
            String username = "inexistente";
            FindUserByUsernameInput input = new FindUserByUsernameInput(principal, username);

            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            assertThrows(
                    UserNotFoundException.class,
                    () -> useCase.execute(input)
            );

            verify(userRepository, times(1)).findByUsername(username);
        }

        @Test
        @DisplayName("Deve propagar exceção quando o repositório lançar um erro inesperado")
        void execute_WhenRepositoryThrowsException_ShouldPropagateException() {
            String username = "erro_banco";
            FindUserByUsernameInput input = new FindUserByUsernameInput(principal, username);

            when(userRepository.findByUsername(username))
                    .thenThrow(new RuntimeException("Falha ao conectar com o banco de dados"));

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> useCase.execute(input)
            );

            assertEquals("Falha ao conectar com o banco de dados", exception.getMessage());
            verify(userRepository, times(1)).findByUsername(username);
        }
    }

    @Nested
    @DisplayName("Casos de Borda e Entradas Nulas ou Vazias")
    class NullAndEdgeCases {

        @Test
        @DisplayName("Deve aceitar principal nulo no input se o UseCase não contiver validação prévia de autorização")
        void execute_WhenPrincipalIsNull_ShouldStillQueryRepository() {
            String username = "joaosilva";
            FindUserByUsernameInput inputWithNullPrincipal = new FindUserByUsernameInput(null, username);

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            FindUserByUsernameOutput output = useCase.execute(inputWithNullPrincipal);

            assertNotNull(output);
            assertEquals(user, output.user());
            verify(userRepository, times(1)).findByUsername(username);
        }

        @Test
        @DisplayName("Deve repassar username nulo do input para o repositório")
        void execute_WhenUsernameInInputIsNull_ShouldQueryRepositoryWithNull() {
            FindUserByUsernameInput nullUsernameInput = new FindUserByUsernameInput(principal, null);

            when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

            assertThrows(
                    UserNotFoundException.class,
                    () -> useCase.execute(nullUsernameInput)
            );

            verify(userRepository, times(1)).findByUsername(null);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   "})
        @DisplayName("Deve repassar username vazio ou com espaços para o repositório e lançar exceção se não encontrado")
        void execute_WhenUsernameIsBlankOrEmpty_ShouldQueryRepository(String blankUsername) {
            FindUserByUsernameInput blankInput = new FindUserByUsernameInput(principal, blankUsername);

            when(userRepository.findByUsername(blankUsername)).thenReturn(Optional.empty());

            assertThrows(
                    UserNotFoundException.class,
                    () -> useCase.execute(blankInput)
            );

            verify(userRepository, times(1)).findByUsername(blankUsername);
        }
    }

    @Nested
    @DisplayName("Especificação de Comportamento e Normalização (Futuro)")
    class MissingBehaviorSpecificationTests {

        @Test
        @DisplayName("ESPECIFICAÇÃO: Verifica se a busca respeita o exato valor do username sem alteração de caixa/trim no usecase")
        void execute_ShouldPassExactUsernameToRepositoryWithoutSanitization() {
            String rawUsername = "  UserTest  ";
            FindUserByUsernameInput input = new FindUserByUsernameInput(principal, rawUsername);

            when(userRepository.findByUsername(rawUsername)).thenReturn(Optional.of(user));

            FindUserByUsernameOutput output = useCase.execute(input);

            assertNotNull(output);
            verify(userRepository, times(1)).findByUsername(rawUsername);
            verify(userRepository, never()).findByUsername("usertest");
        }
    }
}