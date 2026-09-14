package com.letraaletra.api.features.reward.domain;

import com.letraaletra.api.features.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("ItemGrantReward Unit Tests")
class ItemGrantRewardTest {

    @Test
    @DisplayName("não deve tocar wallet (aplicação via agregado novo)")
    void shouldNotTouchWallet() {
        User user = mock(User.class);
        ItemGrantReward reward = new ItemGrantReward(UUID.randomUUID(), 2);

        assertTrue(reward.apply(user).isEmpty());
    }

    @Test
    @DisplayName("deve expor definitionId e quantity")
    void shouldExposeDefinitionIdAndQuantity() {
        UUID definitionId = UUID.randomUUID();
        ItemGrantReward reward = new ItemGrantReward(definitionId, 3);

        assertEquals(definitionId, reward.definitionId());
        assertEquals(3, reward.quantity());
    }
}
