package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.GetMyProfileInput;
import com.letraaletra.api.features.user.application.output.GetMyProfileOutput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetMyProfileUseCase Unit Tests")
class GetMyProfileUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetMyProfileUseCase useCase;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = mock(User.class);
    }

    @Nested
    @DisplayName("Sucesso no Fluxo Principal")
    class SuccessFlows {

        @Test
        @DisplayName("Deve retornar GetMyProfileOutput contendo o perfil do usuário quando o ID for encontrado")
        void execute_WhenUserExists_ShouldReturnOutputWithUser() {
            GetMyProfileInput input = new GetMyProfileInput(userId);

            when(userRepository.find(userId)).thenReturn(Optional.of(user));

            GetMyProfileOutput output = useCase.execute(input);

            assertNotNull(output);
            assertEquals(user, output.user());

            verify(userRepository, times(1)).find(userId);
        }
    }

    @Nested
    @DisplayName("Busca e Exceções de Domínio")
    class UserLookupFailures {

        @Test
        @DisplayName("Deve lançar UserNotFoundException quando o repositório retornar Optional.empty()")
        void execute_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
            GetMyProfileInput input = new GetMyProfileInput(userId);

            when(userRepository.find(userId)).thenReturn(Optional.empty());

            assertThrows(
                    UserNotFoundException.class,
                    () -> useCase.execute(input)
            );

            verify(userRepository, times(1)).find(userId);
        }

        @Test
        @DisplayName("Deve propagar exceção quando o repositório lançar uma falha de infraestrutura")
        void execute_WhenRepositoryThrowsException_ShouldPropagateException() {
            GetMyProfileInput input = new GetMyProfileInput(userId);

            when(userRepository.find(userId))
                    .thenThrow(new RuntimeException("Erro ao consultar banco de dados"));

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> useCase.execute(input)
            );

            assertEquals("Erro ao consultar banco de dados", exception.getMessage());
            verify(userRepository, times(1)).find(userId);
        }
    }

    @Nested
    @DisplayName("Casos de Borda e Entradas Nulas")
    class NullAndEdgeCases {

        @Test
        @DisplayName("Deve repassar ID nulo para o repositório caso venha nulo dentro do input")
        void execute_WhenIdInInputIsNull_ShouldQueryRepositoryWithNull() {
            GetMyProfileInput nullIdInput = new GetMyProfileInput(null);

            when(userRepository.find(null)).thenReturn(Optional.empty());

            assertThrows(
                    UserNotFoundException.class,
                    () -> useCase.execute(nullIdInput)
            );

            verify(userRepository, times(1)).find(null);
        }
    }
}