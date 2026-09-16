package com.letraaletra.api.features.inventory.application.usecase;

import com.letraaletra.api.features.inventory.application.input.ConsumeItemInput;
import com.letraaletra.api.features.inventory.application.output.ConsumeItemOutput;
import com.letraaletra.api.features.items.domain.EffectType;
import com.letraaletra.api.features.inventory.domain.InventoryChangeKind;
import com.letraaletra.api.features.items.domain.ItemCategory;
import com.letraaletra.api.features.items.domain.ItemContext;
import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.items.domain.ItemEffect;
import com.letraaletra.api.features.items.domain.PercentageTimedEffect;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.domain.exception.InsufficientQuantityException;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.items.domain.repository.ItemDefinitionLookup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConsumeItemUseCase Unit Tests")
class ConsumeItemUseCaseTest {

    @Mock
    private ItemDefinitionLookup itemLookup;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder auditRecorder;

    @InjectMocks
    private ConsumeItemUseCase useCase;

    private UUID userId;
    private ItemDefinition boost;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        boost = ItemDefinition.create(
                "XP Boost 50%",
                ItemKind.CONSUMABLE,
                ItemCategory.XP_BOOST,
                ItemContext.PROFILE,
                true,
                1000,
                true,
                new PercentageTimedEffect(EffectType.XP_BOOST_PCT, 50, 60),
                null
        );
    }

    @Test
    @DisplayName("consumo deve persistir e retornar CONSUMED")
    void consumeShouldPersistAndReturnConsumed() {
        when(itemLookup.getById(boost.getId())).thenReturn(boost);
        when(inventoryRepository.findItemsByOwner(userId)).thenReturn(List.of(
                UserItem.restore(userId, boost.getId(), 3, false, LocalDateTime.now(), null)
        ));

        ConsumeItemOutput output = useCase.execute(
                new ConsumeItemInput(userId, boost.getId(), 2));

        assertEquals(InventoryChangeKind.CONSUMED, output.movements().get(0).kind());
        verify(inventoryRepository).deleteItemsByOwner(userId);
        verify(inventoryRepository).saveItem(any(UUID.class), any());
        verify(auditRecorder, atLeastOnce()).record(any(
                com.letraaletra.api.features.audit.domain.AuditEvent.class));
    }

    @Test
    @DisplayName("consumo sem saldo deve falhar sem persistir")
    void consumeWithoutBalanceShouldFailWithoutPersisting() {
        when(itemLookup.getById(boost.getId())).thenReturn(boost);
        when(inventoryRepository.findItemsByOwner(userId)).thenReturn(List.of(
                UserItem.restore(userId, boost.getId(), 1, false, LocalDateTime.now(), null)
        ));

        assertThrows(InsufficientQuantityException.class, () -> useCase.execute(
                new ConsumeItemInput(userId, boost.getId(), 2)));
        verify(inventoryRepository, never()).deleteItemsByOwner(any());
    }
}
