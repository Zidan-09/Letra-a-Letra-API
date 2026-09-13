package com.letraaletra.api.features.friend.infrastructure.presentation.mapper;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.FriendStatus;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.friend.FriendDirection;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.friend.FriendProfileResponse;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.friend.FriendResponse;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.ban.BanInfo;
import com.letraaletra.api.features.user.domain.inventory.Inventory;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import com.letraaletra.api.features.user.domain.stats.UserStats;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FriendProfileMapperTest {

    @Test
    @DisplayName("Deve mapear perfil público com equipados, inGame e ban")
    void toResponse_ShouldMapPublicProfile() {
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);
        when(user.getUserId()).thenReturn(userId);
        when(user.getUsername()).thenReturn("Zidan");
        when(user.isNotInGame()).thenReturn(false);
        when(user.getCurrentGameId()).thenReturn(UUID.randomUUID());
        when(user.getStats()).thenReturn(mock(UserStats.class));
        when(user.getBanInfo()).thenReturn(mock(BanInfo.class));
        when(user.getInventory()).thenReturn(Inventory.restore(List.of(
                InventoryItem.restore(UUID.randomUUID(), "Dragão", CosmeticTypes.AVATAR, true, LocalDateTime.now(), "https://cdn/avatar.webp"),
                InventoryItem.restore(UUID.randomUUID(), "Comum", CosmeticTypes.EMOTE, false, LocalDateTime.now(), null)
        )));

        FriendProfileResponse response = FriendProfileMapper.toResponse(user);

        assertEquals(userId, response.userId());
        assertEquals("Zidan", response.nickname());
        assertTrue(response.inGame());
        assertNotNull(response.currentGameId());
        assertEquals(1, response.equipped().size());
        assertEquals("https://cdn/avatar.webp", response.equipped().getFirst().assetPath());
    }

    @Test
    @DisplayName("Deve anexar profile ao FriendResponse a partir do mapa de usuários")
    void friendResponse_ShouldAttachProfile() {
        UUID viewer = UUID.randomUUID();
        UUID friendId = UUID.randomUUID();
        Friend friendship = Friend.restore(viewer, friendId, FriendStatus.ACCEPT, LocalDateTime.now());

        User friendUser = mock(User.class);
        when(friendUser.getUserId()).thenReturn(friendId);
        when(friendUser.getUsername()).thenReturn("Pombao");
        when(friendUser.isNotInGame()).thenReturn(true);
        when(friendUser.getCurrentGameId()).thenReturn(null);
        when(friendUser.getStats()).thenReturn(mock(UserStats.class));
        when(friendUser.getBanInfo()).thenReturn(mock(BanInfo.class));
        when(friendUser.getInventory()).thenReturn(Inventory.restore(List.of()));

        FriendResponse response = FriendResponseMapper.toResponse(
                friendship, viewer, Map.of(friendId, friendUser));

        assertEquals(friendId, response.friendId());
        assertEquals(FriendDirection.SENT, response.direction());
        assertNotNull(response.profile());
        assertEquals("Pombao", response.profile().nickname());
        assertFalse(response.profile().inGame());
    }

    @Test
    @DisplayName("Deve retornar profile nulo quando o usuário não estiver no mapa")
    void friendResponse_ShouldReturnNullProfileWhenUserMissing() {
        UUID viewer = UUID.randomUUID();
        UUID friendId = UUID.randomUUID();
        Friend friendship = Friend.restore(viewer, friendId, FriendStatus.PENDING, LocalDateTime.now());

        FriendResponse response = FriendResponseMapper.toResponse(
                friendship, viewer, Map.of());

        assertEquals(friendId, response.friendId());
        assertNull(response.profile());
    }
}
