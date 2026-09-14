package com.letraaletra.api.features.participant.domain;

import com.letraaletra.api.features.inventory.domain.ItemCategory;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantTest {

    private Participant participant;
    private final UUID userId = UUID.randomUUID();
    private final String initialSocketId = "session-xyz";
    private final String nickname = "LetraMaster";
    private List<EquippedCosmetic> mockCosmetics;

    @BeforeEach
    void setUp() {
        EquippedCosmetic equippedBanner = new EquippedCosmetic(UUID.randomUUID(), "Banner Dev", ItemCategory.BANNER, true, null);
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

    @Test
    @DisplayName("create/restore devem expor os equipados informados")
    void createAndRestoreShouldExposeEquipped() {
        User user = UserFactory.createLocal("player", "player@test.com", "hash");
        EquippedCosmetic avatar = new EquippedCosmetic(UUID.randomUUID(), "Avatar", ItemCategory.AVATAR, true, "assets/avatar.png");
        EquippedCosmetic banner = new EquippedCosmetic(UUID.randomUUID(), "Banner", ItemCategory.BANNER, false, "assets/banner.png");
        List<EquippedCosmetic> equipped = List.of(avatar, banner);

        Participant created = Participant.create(user, initialSocketId, equipped);
        assertEquals(2, created.getCosmeticsEquipped().size());
        assertEquals(equipped, created.getCosmeticsEquipped());

        Participant restored = Participant.restore(user, equipped);
        assertEquals(2, restored.getCosmeticsEquipped().size());

        assertEquals(created.getCosmeticsEquipped(),
                Participant.create(user.getUserId(), user.getUsername(), initialSocketId, equipped).getCosmeticsEquipped());
    }

    @Test
    @DisplayName("lista de cosméticos deve ser copiada de forma defensiva")
    void cosmeticsListShouldBeDefensiveCopy() {
        User user = UserFactory.createLocal("player", "player@test.com", "hash");
        EquippedCosmetic avatar = new EquippedCosmetic(UUID.randomUUID(), "Avatar", ItemCategory.AVATAR, true, "assets/avatar.png");
        List<EquippedCosmetic> mutable = new ArrayList<>(List.of(avatar));

        Participant created = Participant.create(user, initialSocketId, mutable);
        mutable.clear();

        assertEquals(1, created.getCosmeticsEquipped().size());
        assertEquals(avatar, created.getCosmeticsEquipped().get(0));
        assertThrows(UnsupportedOperationException.class, () -> created.getCosmeticsEquipped().add(avatar));
    }
}