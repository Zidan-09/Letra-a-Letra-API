package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.GetMyInventoryInput;
import com.letraaletra.api.features.user.application.output.GetMyInventoryOutput;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import com.letraaletra.api.features.user.domain.repository.inventory.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetMyInventoryUseCase Unit Tests")
class GetMyInventoryUseCaseTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private GetMyInventoryUseCase useCase;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("Sucesso no Fluxo Principal")
    class SuccessFlows {

        @Test
        @DisplayName("Deve retornar GetMyInventoryOutput contendo a lista de itens quando o usuário possui cosméticos")
        void execute_WhenUserHasCosmetics_ShouldReturnOutputWithItems() {
            InventoryItem item1 = mock(InventoryItem.class);
            InventoryItem item2 = mock(InventoryItem.class);
            List<InventoryItem> expectedItems = List.of(item1, item2);

            GetMyInventoryInput input = new GetMyInventoryInput(userId);

            when(inventoryRepository.getCosmetics(userId)).thenReturn(expectedItems);

            GetMyInventoryOutput output = useCase.execute(input);

            assertNotNull(output);
            assertNotNull(output.inventory());
            assertEquals(2, output.inventory().size());
            assertEquals(expectedItems, output.inventory());

            verify(inventoryRepository, times(1)).getCosmetics(userId);
        }

        @Test
        @DisplayName("Deve retornar GetMyInventoryOutput com lista vazia quando o usuário não possui cosméticos")
        void execute_WhenUserHasNoCosmetics_ShouldReturnEmptyListOutput() {
            GetMyInventoryInput input = new GetMyInventoryInput(userId);

            when(inventoryRepository.getCosmetics(userId)).thenReturn(Collections.emptyList());

            GetMyInventoryOutput output = useCase.execute(input);

            assertNotNull(output);
            assertNotNull(output.inventory());
            assertTrue(output.inventory().isEmpty());

            verify(inventoryRepository, times(1)).getCosmetics(userId);
        }
    }

    @Nested
    @DisplayName("Tratamento de Exceções e Falhas de Repositório")
    class RepositoryFailures {

        @Test
        @DisplayName("Deve propagar exceção lançada pelo repositório ao consultar os cosméticos")
        void execute_WhenRepositoryThrowsException_ShouldPropagateException() {
            GetMyInventoryInput input = new GetMyInventoryInput(userId);

            when(inventoryRepository.getCosmetics(userId))
                    .thenThrow(new RuntimeException("Erro de conexão com o banco de dados"));

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> useCase.execute(input)
            );

            assertEquals("Erro de conexão com o banco de dados", exception.getMessage());
            verify(inventoryRepository, times(1)).getCosmetics(userId);
        }
    }

    @Nested
    @DisplayName("Casos de Borda e Entradas Nulas")
    class NullAndEdgeCases {

        @Test
        @DisplayName("Deve repassar userId nulo para o repositório caso seja informado no input")
        void execute_WhenUserIdInInputIsNull_ShouldQueryRepositoryWithNull() {
            GetMyInventoryInput nullUserIdInput = new GetMyInventoryInput(null);

            when(inventoryRepository.getCosmetics(null)).thenReturn(Collections.emptyList());

            GetMyInventoryOutput output = useCase.execute(nullUserIdInput);

            assertNotNull(output);
            assertTrue(output.inventory().isEmpty());

            verify(inventoryRepository, times(1)).getCosmetics(null);
        }
    }
}