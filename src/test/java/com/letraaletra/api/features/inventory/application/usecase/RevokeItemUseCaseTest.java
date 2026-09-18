package com.letraaletra.api.features.inventory.application.usecase;

import com.letraaletra.api.features.inventory.application.input.RevokeItemInput;
import com.letraaletra.api.features.inventory.application.output.RevokeItemOutput;
import com.letraaletra.api.features.inventory.domain.InventoryChangeKind;
import com.letraaletra.api.features.items.domain.equippable.EquippableCategory;
import com.letraaletra.api.features.items.domain.equippable.EquippableContext;
import com.letraaletra.api.features.items.domain.equippable.EquippableItem;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.domain.exception.ItemNotOwnedException;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.items.domain.repository.ItemLookup;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("RevokeItemUseCase Unit Tests")
class RevokeItemUseCaseTest {

    @Mock
    private ItemLookup itemLookup;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private AdminChecker adminChecker;

    @Mock
    private com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder auditRecorder;

    @InjectMocks
    private RevokeItemUseCase useCase;

    private AuthenticatedUser principal;
    private UUID userId;
    private EquippableItem avatar;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "admin", true, false);
        userId = UUID.randomUUID();
        avatar = EquippableItem.create("Blue Avatar", EquippableContext.PROFILE, EquippableCategory.AVATAR, "/assets/avatar/blue.png");
    }

    @Test
    @DisplayName("revoke deve checar admin, persistir e retornar REMOVED")
    void revokeShouldCheckAdminPersistAndReturnRemoved() {
        when(itemLookup.getById(avatar.getId())).thenReturn(avatar);
        when(inventoryRepository.findItemsByOwner(userId)).thenReturn(List.of(
                UserItem.restore(userId, avatar.getId(), 1, false, LocalDateTime.now(), null)
        ));

        RevokeItemOutput output = useCase.execute(new RevokeItemInput(principal, userId, avatar.getId()));

        verify(adminChecker).check(principal, PermissionKey.USER, PermissionAction.EDIT);
        assertEquals(1, output.movements().size());
        assertEquals(InventoryChangeKind.REMOVED, output.movements().get(0).kind());
        verify(inventoryRepository).deleteItemsByOwner(userId);
        verify(auditRecorder, atLeastOnce()).record(any(
                com.letraaletra.api.features.audit.domain.AuditEvent.class));
    }

    @Test
    @DisplayName("revoke de item não possuído deve falhar sem persistir")
    void revokeNotOwnedShouldFailWithoutPersisting() {
        when(itemLookup.getById(avatar.getId())).thenReturn(avatar);
        when(inventoryRepository.findItemsByOwner(userId)).thenReturn(List.of());

        assertThrows(ItemNotOwnedException.class,
                () -> useCase.execute(new RevokeItemInput(principal, userId, avatar.getId())));
        verify(inventoryRepository, never()).deleteItemsByOwner(any());
    }
}
