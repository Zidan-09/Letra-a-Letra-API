package com.letraaletra.api.features.inventory.application.usecase;

import com.letraaletra.api.features.inventory.application.input.EquipItemInput;
import com.letraaletra.api.features.inventory.application.output.EquipItemOutput;
import com.letraaletra.api.features.inventory.domain.InventoryChangeKind;
import com.letraaletra.api.features.inventory.domain.ItemCategory;
import com.letraaletra.api.features.inventory.domain.ItemContext;
import com.letraaletra.api.features.inventory.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.ItemKind;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.domain.exception.InapplicableContextException;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.inventory.domain.repository.ItemDefinitionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("EquipItemUseCase Unit Tests")
class EquipItemUseCaseTest {

    @Mock
    private ItemDefinitionRepository itemDefinitionRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder auditRecorder;

    @InjectMocks
    private EquipItemUseCase useCase;

    private UUID userId;
    private ItemDefinition avatar;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        avatar = ItemDefinition.create(
                "Blue Avatar",
                ItemKind.COSMETIC,
                ItemCategory.AVATAR,
                Set.of(ItemContext.PROFILE),
                false,
                null,
                false,
                null,
                "/assets/avatar/blue.png"
        );
    }

    @Test
    @DisplayName("equip deve persistir e retornar EQUIPPED")
    void equipShouldPersistAndReturnEquipped() {
        when(itemDefinitionRepository.findById(avatar.getId())).thenReturn(Optional.of(avatar));
        when(inventoryRepository.findItemsByOwner(userId)).thenReturn(List.of(
                UserItem.restore(userId, avatar.getId(), 1, false, LocalDateTime.now(), null)
        ));

        EquipItemOutput output = useCase.execute(
                new EquipItemInput(userId, avatar.getId(), ItemContext.PROFILE));

        assertEquals(1, output.movements().size());
        assertEquals(InventoryChangeKind.EQUIPPED, output.movements().get(0).kind());
        verify(inventoryRepository).deleteItemsByOwner(userId);
        verify(inventoryRepository).saveItem(any(UUID.class), any());
        verify(auditRecorder, atLeastOnce()).record(any(
                com.letraaletra.api.features.audit.domain.AuditEvent.class));
    }

    @Test
    @DisplayName("equip em contexto inválido deve falhar sem persistir")
    void equipInWrongContextShouldFailWithoutPersisting() {
        when(itemDefinitionRepository.findById(avatar.getId())).thenReturn(Optional.of(avatar));
        when(inventoryRepository.findItemsByOwner(userId)).thenReturn(List.of(
                UserItem.restore(userId, avatar.getId(), 1, false, LocalDateTime.now(), null)
        ));

        assertThrows(InapplicableContextException.class, () -> useCase.execute(
                new EquipItemInput(userId, avatar.getId(), ItemContext.MATCH)));
        verify(inventoryRepository, never()).deleteItemsByOwner(any());
    }
}
