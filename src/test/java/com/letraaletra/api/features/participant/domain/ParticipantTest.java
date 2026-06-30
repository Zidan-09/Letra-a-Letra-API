package com.letraaletra.api.features.participant.domain;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantTest {

    private Participant participant;
    private final UUID userId = UUID.randomUUID();
    private final String initialSocketId = "session-xyz";
    private final String nickname = "LetraMaster";
    private List<InventoryItem> mockCosmetics;

    @BeforeEach
    void setUp() {
        InventoryItem equippedBanner = new InventoryItem(UUID.randomUUID(), "Banner Dev", CosmeticTypes.BANNER, true, LocalDateTime.now());
        mockCosmetics = List.of(equippedBanner);

        participant = new Participant(userId, initialSocketId, nickname, mockCosmetics);
    }

    @Test
    @DisplayName("Deve inicializar o participante com os valores corretos e papel padrão de SPECTATOR")
    void shouldInitializeWithCorrectDefaults() {
        assertEquals(userId, participant.getUserId());
        assertEquals(initialSocketId, participant.getSocketId());
        assertEquals(nickname, participant.getNickname());
        assertEquals(mockCosmetics, participant.getCosmeticsEquipped());
        assertTrue(participant.isConnected(), "O participante deve iniciar conectado por padrão");
        assertEquals(ParticipantRole.SPECTATOR, participant.getRole(), "O papel inicial deve ser SPECTATOR");
    }

    @Test
    @DisplayName("Deve alterar o papel do participante com sucesso")
    void shouldChangeParticipantRole() {
        assertEquals(ParticipantRole.SPECTATOR, participant.getRole());

        participant.changeRole(ParticipantRole.PLAYER);
        assertEquals(ParticipantRole.PLAYER, participant.getRole());

        participant.changeRole(ParticipantRole.SPECTATOR);
        assertEquals(ParticipantRole.SPECTATOR, participant.getRole());
    }

    @Test
    @DisplayName("Deve alterar o estado para desconectado ao executar disconnect")
    void shouldHandleDisconnect() {
        assertTrue(participant.isConnected());

        participant.disconnect();

        assertFalse(participant.isConnected(), "O estado de conexão deve ser alterado para false");
        assertEquals(initialSocketId, participant.getSocketId());
    }

    @Test
    @DisplayName("Deve reconectar o participante atualizando o estado e o ID da sessão do socket")
    void shouldHandleConnectOrReconnection() {
        participant.disconnect();
        assertFalse(participant.isConnected());

        String newSessionId = "session-abc-456";
        participant.connect(newSessionId);

        assertTrue(participant.isConnected(), "O estado de conexão deve voltar a ser true");
        assertEquals(newSessionId, participant.getSocketId(), "O socketId deve ser atualizado com a nova sessão");
    }
}