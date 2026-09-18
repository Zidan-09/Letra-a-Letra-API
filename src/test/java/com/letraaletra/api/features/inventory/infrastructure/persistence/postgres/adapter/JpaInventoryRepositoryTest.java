package com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.entity.UserItemJpaEntity;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.jpa.SpringDataUserItemRepository;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.projection.UserItemProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class JpaInventoryRepositoryTest {

    @Mock
    private SpringDataUserItemRepository springData;

    private JpaInventoryRepository adapter;
    private final UUID ownerId = UUID.randomUUID();
    private final UUID itemId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        adapter = new JpaInventoryRepository(springData);
    }

    @Test
    @DisplayName("findItemsByOwner deve mapear projections para o domínio")
    void findItemsByOwnerShouldMapProjections() {
        LocalDateTime acquiredAt = LocalDateTime.now();
        UserItemProjection projection = mock(UserItemProjection.class);
        when(projection.getUserId()).thenReturn(ownerId);
        when(projection.getItemId()).thenReturn(itemId);
        when(projection.getQuantity()).thenReturn(3);
        when(projection.isEquipped()).thenReturn(false);
        when(projection.getAcquiredAt()).thenReturn(acquiredAt);
        when(projection.getExpiresAt()).thenReturn(null);
        when(springData.findItemsByOwner(ownerId)).thenReturn(List.of(projection));

        List<UserItem> items = adapter.findItemsByOwner(ownerId);

        assertEquals(1, items.size());
        assertEquals(itemId, items.get(0).getItemId());
        assertEquals(3, items.get(0).getQuantity());
        assertEquals(acquiredAt, items.get(0).getAcquiredAt());
    }

    @Test
    @DisplayName("saveItem deve mapear e delegar ao Spring Data")
    void saveItemShouldMapAndDelegate() {
        UserItem item = UserItem.restore(ownerId, itemId, 2, true, LocalDateTime.now(), null);

        adapter.saveItem(ownerId, item);

        ArgumentCaptor<UserItemJpaEntity> captor = ArgumentCaptor.forClass(UserItemJpaEntity.class);
        verify(springData).save(captor.capture());
        assertEquals(ownerId, captor.getValue().getUserItemId().getUserId());
        assertEquals(itemId, captor.getValue().getUserItemId().getItemId());
        assertEquals(2, captor.getValue().getQuantity());
        assertTrue(captor.getValue().isEquipped());
    }

    @Test
    @DisplayName("deleteItemsByOwner deve delegar ao Spring Data")
    void deleteItemsByOwnerShouldDelegate() {
        adapter.deleteItemsByOwner(ownerId);

        verify(springData).deleteItemsByOwner(ownerId);
    }
}
