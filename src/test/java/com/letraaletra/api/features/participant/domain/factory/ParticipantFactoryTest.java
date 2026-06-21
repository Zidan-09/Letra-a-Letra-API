package com.letraaletra.api.features.participant.domain.factory;

import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.participant.domain.ParticipantRole;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.factory.UserFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantFactoryTest {

    @Test
    @DisplayName("fromUser: Deve criar um participante mapeando as info do User e filtrando apenas cosméticos equipados")
    void shouldCreateParticipantFromUserFilteringCosmetics() {
        UserFactory userFactory = new UserFactory();

        User user = userFactory.createLocal("SamuelDev", "samuel@letra.com", "hash");
        String sessionId = "ws-session-123";

        Participant participant = ParticipantFactory.fromUser(user, sessionId);

        assertNotNull(participant);
        assertEquals("SamuelDev", participant.getNickname());
        assertEquals(sessionId, participant.getSocketId());
        assertTrue(participant.isConnected());
        assertEquals(ParticipantRole.SPECTATOR, participant.getRole());
    }
}