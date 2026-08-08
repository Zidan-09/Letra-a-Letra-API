package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.ChangeCosmeticInput;
import com.letraaletra.api.features.user.application.output.ChangeCosmeticOutput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.inventory.Inventory;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChangeCosmeticUseCase Unit Tests")
class ChangeCosmeticUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChangeCosmeticUseCase useCase;

    private UUID userId;
    private UUID cosmeticId;
    private ChangeCosmeticInput input;
    private User user;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        cosmeticId = UUID.randomUUID();
        input = new ChangeCosmeticInput(userId, cosmeticId);
        user = mock(User.class);
        inventory = mock(Inventory.class);
    }

    @Nested
    @DisplayName("Sucesso no Fluxo Principal")
    class SuccessFlows {

        @Test
        @DisplayName("Deve equipar cosmético, salvar o usuário e retornar o output contendo o usuário")
        void execute_WhenUserExistsAndCosmeticIsValid_ShouldEquipSaveAndReturnOutput() {
            when(userRepository.find(userId)).thenReturn(Optional.of(user));
            when(user.getInventory()).thenReturn(inventory);

            ChangeCosmeticOutput output = useCase.execute(input);

            assertNotNull(output);
            assertEquals(user, output.user());

            InOrder inOrder = inOrder(userRepository, user, inventory);
            inOrder.verify(userRepository).find(userId);
            inOrder.verify(user).getInventory();
            inOrder.verify(inventory).equipCosmetic(cosmeticId);
            inOrder.verify(userRepository).save(user);

            verifyNoMoreInteractions(userRepository, user, inventory);
        }
    }

    @Nested
    @DisplayName("Busca e Existência de Usuário")
    class UserLookupFailures {

        @Test
        @DisplayName("Deve lançar UserNotFoundException quando o usuário não for encontrado no repositório")
        void execute_WhenUserNotFound_ShouldThrowUserNotFoundException() {
            when(userRepository.find(userId)).thenReturn(Optional.empty());

            assertThrows(
                    UserNotFoundException.class,
                    () -> useCase.execute(input)
            );

            verify(userRepository, times(1)).find(userId);
            verify(userRepository, never()).save(any());
            verifyNoInteractions(user, inventory);
        }
    }

    @Nested
    @DisplayName("Regras de Domínio e Integridade do Inventário")
    class DomainLogicAndInvariants {

        @Test
        @DisplayName("Deve interromper a execução e não salvar quando getInventory() for nulo")
        void execute_WhenInventoryIsNull_ShouldThrowNullPointerExceptionAndNotSave() {
            when(userRepository.find(userId)).thenReturn(Optional.of(user));
            when(user.getInventory()).thenReturn(null);

            assertThrows(
                    NullPointerException.class,
                    () -> useCase.execute(input)
            );

            verify(userRepository, times(1)).find(userId);
            verify(user, times(1)).getInventory();
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve interromper o fluxo e não salvar o usuário se o método equipCosmetic lançar exceção de domínio")
        void execute_WhenEquipCosmeticFails_ShouldNotSaveUser() {
            when(userRepository.find(userId)).thenReturn(Optional.of(user));
            when(user.getInventory()).thenReturn(inventory);

            doThrow(new IllegalArgumentException("Cosmético não encontrado no inventário do usuário"))
                    .when(inventory).equipCosmetic(cosmeticId);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> useCase.execute(input)
            );

            assertEquals("Cosmético não encontrado no inventário do usuário", exception.getMessage());
            verify(userRepository, times(1)).find(userId);
            verify(inventory, times(1)).equipCosmetic(cosmeticId);
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Falhas na Camada de Persistência (Repositório)")
    class RepositoryFailures {

        @Test
        @DisplayName("Deve propagar exceção quando o repositório falhar na operação save()")
        void execute_WhenUserRepositorySaveFails_ShouldPropagateException() {
            when(userRepository.find(userId)).thenReturn(Optional.of(user));
            when(user.getInventory()).thenReturn(inventory);

            doThrow(new RuntimeException("Erro ao conectar ao banco de dados"))
                    .when(userRepository).save(user);

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> useCase.execute(input)
            );

            assertEquals("Erro ao conectar ao banco de dados", exception.getMessage());
            verify(userRepository, times(1)).find(userId);
            verify(inventory, times(1)).equipCosmetic(cosmeticId);
            verify(userRepository, times(1)).save(user);
        }
    }

    @Nested
    @DisplayName("Casos de Borda e Entradas Nulas")
    class NullAndEdgeCases {
        @Test
        @DisplayName("Deve repassar userId nulo do input para o repositório")
        void execute_WhenUserIdInInputIsNull_ShouldQueryRepositoryWithNull() {
            ChangeCosmeticInput nullUserIdInput = new ChangeCosmeticInput(null, cosmeticId);
            when(userRepository.find(null)).thenReturn(Optional.empty());

            assertThrows(
                    UserNotFoundException.class,
                    () -> useCase.execute(nullUserIdInput)
            );

            verify(userRepository, times(1)).find(null);
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve repassar cosmeticId nulo para o método equipCosmetic do inventário")
        void execute_WhenCosmeticIdIsNull_ShouldPassNullToEquipCosmetic() {
            ChangeCosmeticInput nullCosmeticInput = new ChangeCosmeticInput(userId, null);
            when(userRepository.find(userId)).thenReturn(Optional.of(user));
            when(user.getInventory()).thenReturn(inventory);

            useCase.execute(nullCosmeticInput);

            verify(inventory, times(1)).equipCosmetic(null);
            verify(userRepository, times(1)).save(user);
        }
    }
}