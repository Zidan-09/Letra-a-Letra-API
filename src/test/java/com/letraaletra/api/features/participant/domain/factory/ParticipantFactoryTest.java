package com.letraaletra.api.features.participant.domain.factory;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.participant.domain.ParticipantRole;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.factory.UserFactory;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantFactoryTest {

    @Test
    @DisplayName("fromUser: Deve criar um participante mapeando as info do User e filtrando apenas cosméticos equipados")
    void shouldCreateParticipantFromUserFilteringCosmetics() {
        UserFactory userFactory = new UserFactory();
        User user = userFactory.createLocal("user-uuid", "SamuelDev", "samuel@letra.com", "hash");

        LocalDateTime now = LocalDateTime.now();
        InventoryItem itemEquipado = new InventoryItem("id-1", "Avatar Cartoon", CosmeticTypes.AVATAR, true, now);
        InventoryItem itemNaoEquipado = new InventoryItem("id-2", "Banner Antigo", CosmeticTypes.BANNER, false, now);

        user.setInventory(List.of(itemEquipado, itemNaoEquipado));
        String sessionId = "ws-session-123";

        Participant participant = ParticipantFactory.fromUser(user, sessionId);

        assertNotNull(participant);
        assertEquals("user-uuid", participant.getUserId());
        assertEquals("SamuelDev", participant.getNickname());
        assertEquals(sessionId, participant.getSocketId());
        assertTrue(participant.isConnected());
        assertEquals(ParticipantRole.SPECTATOR, participant.getRole());

        List<InventoryItem> cosmetics = participant.getCosmeticsEquipped();
        assertEquals(1, cosmetics.size(), "Deve conter apenas o item que estava marcado como equipado");
        assertEquals("id-1", cosmetics.getFirst().cosmetic_id());
        assertTrue(cosmetics.getFirst().equipped(), "O item remanescente deve ser o equipado");
    }
}