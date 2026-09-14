package com.letraaletra.api.features.inventory.application.usecase;

import com.letraaletra.api.features.inventory.application.input.GetInventoryInput;
import com.letraaletra.api.features.inventory.application.output.GetInventoryOutput;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetInventoryUseCase Unit Tests")
class GetInventoryUseCaseTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private GetInventoryUseCase useCase;

    @Test
    @DisplayName("deve retornar os itens do dono")
    void shouldReturnOwnerItems() {
        UUID userId = UUID.randomUUID();
        List<UserItem> items = List.of(
                UserItem.restore(userId, UUID.randomUUID(), 2, false, LocalDateTime.now(), null)
        );
        when(inventoryRepository.findItemsByOwner(userId)).thenReturn(items);

        GetInventoryOutput output = useCase.execute(new GetInventoryInput(userId));

        assertEquals(items, output.items());
        verify(inventoryRepository).findItemsByOwner(userId);
    }

    @Test
    @DisplayName("dono sem itens deve retornar lista vazia")
    void ownerWithoutItemsShouldReturnEmptyList() {
        UUID userId = UUID.randomUUID();
        when(inventoryRepository.findItemsByOwner(userId)).thenReturn(List.of());

        GetInventoryOutput output = useCase.execute(new GetInventoryInput(userId));

        assertTrue(output.items().isEmpty());
    }
}
