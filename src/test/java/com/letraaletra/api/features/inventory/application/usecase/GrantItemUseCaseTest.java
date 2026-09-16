package com.letraaletra.api.features.inventory.application.usecase;

import com.letraaletra.api.features.inventory.application.input.GrantItemInput;
import com.letraaletra.api.features.inventory.application.output.GrantItemOutput;
import com.letraaletra.api.features.inventory.domain.InventoryChangeKind;
import com.letraaletra.api.features.items.domain.ItemCategory;
import com.letraaletra.api.features.items.domain.ItemContext;
import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.features.inventory.domain.exception.DuplicateUniqueItemException;
import com.letraaletra.api.features.items.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.items.domain.repository.ItemDefinitionLookup;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
@DisplayName("GrantItemUseCase Unit Tests")
class GrantItemUseCaseTest {

    @Mock
    private ItemDefinitionLookup itemLookup;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private AdminChecker adminChecker;

    @Mock
    private com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder auditRecorder;

    @InjectMocks
    private GrantItemUseCase useCase;

    private AuthenticatedUser principal;
    private UUID userId;
    private ItemDefinition avatar;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "admin", true, false);
        userId = UUID.randomUUID();
        avatar = ItemDefinition.create(
                "Blue Avatar",
                ItemKind.COSMETIC,
                ItemCategory.AVATAR,
                ItemContext.PROFILE,
                false,
                null,
                false,
                null,
                "/assets/avatar/blue.png"
        );
    }

    @Test
    @DisplayName("grant deve checar admin, persistir e retornar ACQUIRED")
    void grantShouldCheckAdminPersistAndReturnAcquired() {
        when(itemLookup.getById(avatar.getId())).thenReturn(avatar);
        when(inventoryRepository.findItemsByOwner(userId)).thenReturn(List.of());

        GrantItemOutput output = useCase.execute(new GrantItemInput(principal, userId, avatar.getId(), 1));

        verify(adminChecker).check(principal, PermissionKey.USER, PermissionAction.EDIT);
        assertEquals(1, output.movements().size());
        assertEquals(InventoryChangeKind.ACQUIRED, output.movements().get(0).kind());
        verify(inventoryRepository).deleteItemsByOwner(userId);
        verify(inventoryRepository).saveItem(any(UUID.class), any());
        verify(auditRecorder, atLeastOnce()).record(any(
                com.letraaletra.api.features.audit.domain.AuditEvent.class));
    }

    @Test
    @DisplayName("definição inexistente deve falhar sem persistir")
    void missingDefinitionShouldFailWithoutPersisting() {
        UUID itemId = UUID.randomUUID();
        when(itemLookup.getById(itemId)).thenThrow(new ItemNotFoundException());

        assertThrows(ItemNotFoundException.class,
                () -> useCase.execute(new GrantItemInput(principal, userId, itemId, 1)));
        verify(inventoryRepository, never()).deleteItemsByOwner(any());
    }

    @Test
    @DisplayName("duplicata de item único deve falhar sem persistir")
    void duplicateUniqueShouldFailWithoutPersisting() {
        when(itemLookup.getById(avatar.getId())).thenReturn(avatar);
        when(inventoryRepository.findItemsByOwner(userId)).thenReturn(List.of(
                com.letraaletra.api.features.inventory.domain.UserItem.restore(
                        userId, avatar.getId(), 1, false, java.time.LocalDateTime.now(), null)
        ));

        assertThrows(DuplicateUniqueItemException.class,
                () -> useCase.execute(new GrantItemInput(principal, userId, avatar.getId(), 1)));
        verify(inventoryRepository, never()).deleteItemsByOwner(any());
    }
}
