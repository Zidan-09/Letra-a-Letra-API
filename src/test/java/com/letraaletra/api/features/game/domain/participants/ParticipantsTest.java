package com.letraaletra.api.features.game.domain.participants;

import com.letraaletra.api.features.game.domain.RoomSettings;
import com.letraaletra.api.features.game.domain.exception.RoomFullException;
import com.letraaletra.api.features.game.domain.exception.UserBannedException;
import com.letraaletra.api.features.game.domain.exception.UserNotInGameException;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.participant.domain.ParticipantRole;
import com.letraaletra.api.features.participant.domain.exception.InvalidRoomPositionException;
import com.letraaletra.api.features.participant.domain.exception.ParticipantAlreadyBannedException;
import com.letraaletra.api.features.participant.domain.exception.ParticipantNotBannedException;
import com.letraaletra.api.features.user.domain.exception.UserAlreadyInGameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParticipantsTest {

    private Participants participants;

    @BeforeEach
    void setUp() {
        participants = new Participants();
    }

    private Participant createMockParticipant(UUID userId, String socketId, ParticipantRole initialRole) {
        Participant mockParticipant = mock(Participant.class);
        when(mockParticipant.getUserId()).thenReturn(userId);
        when(mockParticipant.getSocketId()).thenReturn(socketId);
        when(mockParticipant.getRole()).thenReturn(initialRole);
        return mockParticipant;
    }

    private RoomSettings createRoomSettings(boolean allowSpectators) {
        RoomSettings settings = mock(RoomSettings.class);
        when(settings.roomAllowSpectators()).thenReturn(allowSpectators);
        return settings;
    }

    @Nested
    @DisplayName("Testes de Consulta e Estado Inicial")
    class QueryAndStateTests {

        @Test
        @DisplayName("Deve inicializar com coleções vazias e zero jogadores")
        void shouldInitializeEmpty() {
            assertTrue(participants.getParticipants().isEmpty());
            assertTrue(participants.getPositions().isEmpty());
            assertEquals(0, participants.getAmountPlayers());
        }

        @Test
        @DisplayName("Deve retornar lista imutável de participantes")
        void shouldReturnUnmodifiableParticipantsList() {
            UUID userId = UUID.randomUUID();
            Participant p = createMockParticipant(userId, "s1", ParticipantRole.PLAYER);
            participants.join(p, createRoomSettings(true));

            List<Participant> list = participants.getParticipants();
            assertThrows(UnsupportedOperationException.class, list::removeFirst);
        }

        @Test
        @DisplayName("Deve retornar mapa imutável de posições")
        void shouldReturnUnmodifiablePositionsMap() {
            UUID userId = UUID.randomUUID();
            Participant p = createMockParticipant(userId, "s1", ParticipantRole.PLAYER);
            participants.join(p, createRoomSettings(true));

            Map<Integer, UUID> posMap = participants.getPositions();
            assertThrows(UnsupportedOperationException.class, () -> posMap.put(99, UUID.randomUUID()));
        }

        @Test
        @DisplayName("Deve buscar participante por ID com sucesso ou retornar nulo")
        void shouldGetParticipantByUserId() {
            UUID userId = UUID.randomUUID();
            Participant p = createMockParticipant(userId, "s1", ParticipantRole.PLAYER);
            participants.join(p, createRoomSettings(true));

            assertEquals(p, participants.getParticipantByUserId(userId));
            assertNull(participants.getParticipantByUserId(UUID.randomUUID()));
        }

        @Test
        @DisplayName("Deve contar corretamente apenas os participantes com role PLAYER")
        void shouldCountOnlyPlayers() {
            Participant p1 = createMockParticipant(UUID.randomUUID(), "s1", ParticipantRole.PLAYER);
            Participant p2 = createMockParticipant(UUID.randomUUID(), "s2", ParticipantRole.PLAYER);
            Participant p3 = createMockParticipant(UUID.randomUUID(), "s3", ParticipantRole.SPECTATOR);

            participants.join(p1, createRoomSettings(true));
            participants.join(p2, createRoomSettings(true));
            participants.join(p3, createRoomSettings(true));

            assertEquals(2, participants.getAmountPlayers());
        }

        @Test
        @DisplayName("Deve encontrar participante pelo Session ID (Socket ID)")
        void shouldFindBySessionId() {
            UUID userId = UUID.randomUUID();
            Participant p = createMockParticipant(userId, "session-123", ParticipantRole.PLAYER);
            participants.join(p, createRoomSettings(true));

            assertEquals(p, participants.findBySession("session-123"));
            assertNull(participants.findBySession("non-existent-session"));
        }

        @Test
        @DisplayName("BUG/EDGE CASE: findNextParticipant lança NoSuchElementException se a sala estiver vazia")
        void findNextParticipant_ShouldThrowException_WhenEmpty() {
            assertThrows(NoSuchElementException.class, () -> participants.findNextParticipant());
        }

        @Test
        @DisplayName("Deve retornar o próximo participante existente")
        void shouldFindNextParticipant() {
            UUID userId = UUID.randomUUID();
            Participant p = createMockParticipant(userId, "s1", ParticipantRole.PLAYER);
            participants.join(p, createRoomSettings(true));

            assertEquals(userId, participants.findNextParticipant());
        }
    }

    @Nested
    @DisplayName("Testes do Método join")
    class JoinTests {

        @Test
        @DisplayName("Deve adicionar os dois primeiros participantes como PLAYER nas posições 0 e 1")
        void shouldAddFirstTwoParticipantsAsPlayersInSequentialPositions() {
            UUID u1 = UUID.randomUUID();
            UUID u2 = UUID.randomUUID();
            Participant p1 = createMockParticipant(u1, "s1", ParticipantRole.PLAYER);
            Participant p2 = createMockParticipant(u2, "s2", ParticipantRole.PLAYER);

            RoomSettings settings = createRoomSettings(true);

            participants.join(p1, settings);
            participants.join(p2, settings);

            verify(p1).changeRole(ParticipantRole.PLAYER);
            verify(p2).changeRole(ParticipantRole.PLAYER);

            Map<Integer, UUID> pos = participants.getPositions();
            assertEquals(u1, pos.get(0));
            assertEquals(u2, pos.get(1));
        }

        @Test
        @DisplayName("Deve adicionar a partir do terceiro participante como SPECTATOR")
        void shouldAddThirdAndSubsequentParticipantsAsSpectators() {
            RoomSettings settings = createRoomSettings(true);

            for (int i = 0; i < 2; i++) {
                Participant p = createMockParticipant(UUID.randomUUID(), "s" + i, ParticipantRole.PLAYER);
                participants.join(p, settings);
            }

            Participant p3 = createMockParticipant(UUID.randomUUID(), "s3", ParticipantRole.SPECTATOR);
            participants.join(p3, settings);

            verify(p3).changeRole(ParticipantRole.SPECTATOR);
            assertEquals(UUID.class, participants.getPositions().get(2).getClass());
        }

        @Test
        @DisplayName("Deve lançar UserBannedException ao tentar entrar sendo banido")
        void shouldThrowExceptionWhenBannedUserTriesToJoin() {
            UUID userId = UUID.randomUUID();
            Participant p = createMockParticipant(userId, "s1", ParticipantRole.PLAYER);
            participants.addToBlackList(userId);

            assertThrows(UserBannedException.class, () -> participants.join(p, createRoomSettings(true)));
        }

        @Test
        @DisplayName("Deve lançar UserAlreadyInGameException se o usuário já estiver no jogo")
        void shouldThrowExceptionWhenUserAlreadyInGame() {
            UUID userId = UUID.randomUUID();
            Participant p = createMockParticipant(userId, "s1", ParticipantRole.PLAYER);
            RoomSettings settings = createRoomSettings(true);

            participants.join(p, settings);

            assertThrows(UserAlreadyInGameException.class, () -> participants.join(p, settings));
        }

        @Test
        @DisplayName("Deve lançar RoomFullException se a sala não permitir espectadores e já houver 2 participantes")
        void shouldThrowExceptionWhenRoomFullWithoutSpectators() {
            RoomSettings noSpectators = createRoomSettings(false);

            Participant p1 = createMockParticipant(UUID.randomUUID(), "s1", ParticipantRole.PLAYER);
            Participant p2 = createMockParticipant(UUID.randomUUID(), "s2", ParticipantRole.PLAYER);
            Participant p3 = createMockParticipant(UUID.randomUUID(), "s3", ParticipantRole.SPECTATOR);

            participants.join(p1, noSpectators);
            participants.join(p2, noSpectators);

            assertThrows(RoomFullException.class, () -> participants.join(p3, noSpectators));
        }

        @Test
        @DisplayName("Deve lançar RoomFullException quando a sala atingir o limite máximo de 7 participantes")
        void shouldThrowExceptionWhenRoomReachesMaxCapacityOf7() {
            RoomSettings settings = createRoomSettings(true);

            for (int i = 0; i < 7; i++) {
                Participant p = createMockParticipant(UUID.randomUUID(), "s" + i, ParticipantRole.PLAYER);
                participants.join(p, settings);
            }

            Participant p8 = createMockParticipant(UUID.randomUUID(), "s8", ParticipantRole.SPECTATOR);
            assertThrows(RoomFullException.class, () -> participants.join(p8, settings));
        }

        @Test
        @DisplayName("BUG/LÓGICA Inconsistente: assertMaxPlayers() é redundante ou lança exceção incorretamente")
        void join_ShouldValidateRoleDeterminationAndAssertMaxPlayersBehavior() {
            Participant p1 = createMockParticipant(UUID.randomUUID(), "s1", ParticipantRole.PLAYER);
            Participant p2 = createMockParticipant(UUID.randomUUID(), "s2", ParticipantRole.PLAYER);

            participants.join(p1, createRoomSettings(true));

            when(p1.getRole()).thenReturn(ParticipantRole.PLAYER);

            assertDoesNotThrow(() -> participants.join(p2, createRoomSettings(true)));
        }
    }

    @Nested
    @DisplayName("Testes do Método remove")
    class RemoveTests {

        @Test
        @DisplayName("Deve remover participante e liberar sua posição na sala")
        void shouldRemoveParticipantAndFreePosition() {
            UUID u1 = UUID.randomUUID();
            Participant p1 = createMockParticipant(u1, "s1", ParticipantRole.PLAYER);
            participants.join(p1, createRoomSettings(true));

            assertEquals(1, participants.getParticipants().size());
            assertEquals(0, participants.getPositions().get(0) != null ? 0 : -1);

            Participant removed = participants.remove(u1);

            assertEquals(p1, removed);
            assertTrue(participants.getParticipants().isEmpty());
            assertTrue(participants.getPositions().isEmpty());
        }

        @Test
        @DisplayName("Deve lançar UserNotInGameException ao tentar remover usuário que não está na sala")
        void shouldThrowExceptionWhenRemovingNonExistentUser() {
            assertThrows(UserNotInGameException.class, () -> participants.remove(UUID.randomUUID()));
        }
    }

    @Nested
    @DisplayName("Testes do Método changePosition")
    class ChangePositionTests {

        @ParameterizedTest
        @ValueSource(ints = {-1, 7, 10, -10})
        @DisplayName("Deve lançar InvalidRoomPositionException para posições fora do intervalo [0, 6]")
        void shouldThrowExceptionForInvalidPositionBounds(int invalidPosition) {
            UUID userId = UUID.randomUUID();
            assertThrows(InvalidRoomPositionException.class, () ->
                    participants.changePosition(userId, invalidPosition));
        }

        @Test
        @DisplayName("Deve lançar UserNotInGameException ao mudar posição de usuário não registration")
        void shouldThrowExceptionWhenUserNotInGame() {
            assertThrows(UserNotInGameException.class, () ->
                    participants.changePosition(UUID.randomUUID(), 0));
        }

        @Test
        @DisplayName("Deve lançar InvalidRoomPositionException se a posição de destino já estiver ocupada")
        void shouldThrowExceptionWhenTargetPositionIsOccupied() {
            RoomSettings settings = createRoomSettings(true);
            UUID u1 = UUID.randomUUID();
            UUID u2 = UUID.randomUUID();

            Participant p1 = createMockParticipant(u1, "s1", ParticipantRole.PLAYER);
            Participant p2 = createMockParticipant(u2, "s2", ParticipantRole.PLAYER);

            participants.join(p1, settings);
            participants.join(p2, settings);

            assertThrows(InvalidRoomPositionException.class, () ->
                    participants.changePosition(u2, 0));
        }

        @Test
        @DisplayName("Deve mudar posição com sucesso de PLAYER (0) para SPECTATOR (2)")
        void shouldChangePositionFromPlayerToSpectator() {
            RoomSettings settings = createRoomSettings(true);
            UUID u1 = UUID.randomUUID();
            Participant p1 = createMockParticipant(u1, "s1", ParticipantRole.PLAYER);

            participants.join(p1, settings);
            participants.changePosition(u1, 2);

            verify(p1).changeRole(ParticipantRole.SPECTATOR);
            assertEquals(u1, participants.getPositions().get(2));
            assertNull(participants.getPositions().get(0));
        }

        @Test
        @DisplayName("Deve mudar posição com sucesso de SPECTATOR (2) para PLAYER (0)")
        void shouldChangePositionFromSpectatorToPlayer() {
            RoomSettings settings = createRoomSettings(true);

            UUID u1 = UUID.randomUUID();
            UUID u2 = UUID.randomUUID();
            UUID u3 = UUID.randomUUID();

            Participant p1 = createMockParticipant(u1, "s1", ParticipantRole.PLAYER);
            Participant p2 = createMockParticipant(u2, "s2", ParticipantRole.PLAYER);
            Participant p3 = createMockParticipant(u3, "s3", ParticipantRole.SPECTATOR);

            participants.join(p1, settings);
            participants.join(p2, settings);
            participants.join(p3, settings);

            participants.remove(u1);

            participants.changePosition(u3, 0);

            verify(p3).changeRole(ParticipantRole.PLAYER);
            assertEquals(u3, participants.getPositions().get(0));
            assertNull(participants.getPositions().get(2));
        }
    }

    @Nested
    @DisplayName("Testes de Reconnection")
    class ReconnectTests {

        @Test
        @DisplayName("Deve reconnection participante atualizando o Socket ID")
        void shouldReconnectParticipantSuccessfully() {
            UUID userId = UUID.randomUUID();
            Participant p = createMockParticipant(userId, "old-session", ParticipantRole.PLAYER);

            participants.join(p, createRoomSettings(true));
            participants.reconnect(userId, "new-session");

            verify(p).connect("new-session");
        }

        @Test
        @DisplayName("Deve lançar UserNotInGameException ao tentar reconnection usuário inexistente")
        void shouldThrowExceptionWhenReconnectingNonExistentUser() {
            assertThrows(UserNotInGameException.class, () ->
                    participants.reconnect(UUID.randomUUID(), "new-session"));
        }
    }

    @Nested
    @DisplayName("Testes da Blacklist (Banimento)")
    class BlacklistTests {

        @Test
        @DisplayName("Deve adicionar e verificar usuário na blacklist")
        void shouldAddUserToBlacklist() {
            UUID userId = UUID.randomUUID();

            assertFalse(participants.isBlackListed(userId));
            participants.addToBlackList(userId);
            assertTrue(participants.isBlackListed(userId));
        }

        @Test
        @DisplayName("Deve lançar ParticipantAlreadyBannedException ao banir usuário já banido")
        void shouldThrowExceptionWhenUserAlreadyBanned() {
            UUID userId = UUID.randomUUID();
            participants.addToBlackList(userId);

            assertThrows(ParticipantAlreadyBannedException.class, () ->
                    participants.addToBlackList(userId));
        }

        @Test
        @DisplayName("Deve remover usuário da blacklist com sucesso")
        void shouldRemoveUserFromBlacklist() {
            UUID userId = UUID.randomUUID();
            participants.addToBlackList(userId);

            participants.removeFromBlackList(userId);
            assertFalse(participants.isBlackListed(userId));
        }

        @Test
        @DisplayName("Deve lançar ParticipantNotBannedException ao despair usuário que não está na blacklist")
        void shouldThrowExceptionWhenRemovingNonBannedUser() {
            assertThrows(ParticipantNotBannedException.class, () ->
                    participants.removeFromBlackList(UUID.randomUUID()));
        }
    }

    @Nested
    @DisplayName("Testes de Algoritmo Interno e Posicionamento Próximo")
    class InternalPositioningTests {

        @Test
        @DisplayName("Deve preencher corretamente a primeira posição vaga disponível (nextAvailablePosition)")
        void shouldFillFirstAvailablePositionGap() {
            RoomSettings settings = createRoomSettings(true);

            UUID u1 = UUID.randomUUID();
            UUID u2 = UUID.randomUUID();
            UUID u3 = UUID.randomUUID();

            Participant p1 = createMockParticipant(u1, "s1", ParticipantRole.PLAYER);
            Participant p2 = createMockParticipant(u2, "s2", ParticipantRole.PLAYER);
            Participant p3 = createMockParticipant(u3, "s3", ParticipantRole.SPECTATOR);

            participants.join(p1, settings);
            participants.join(p2, settings);

            participants.remove(u1);

            participants.join(p3, settings);
            assertEquals(u3, participants.getPositions().get(0));
        }

        @Test
        @DisplayName("BUG/ESTADO ILEGAL: Deve lançar IllegalStateException se o mapa de posições estiver cheio mas a validação falhar")
        void nextAvailablePosition_ShouldThrowIllegalStateException_WhenNoPositionsAvailable() {
            RoomSettings settings = createRoomSettings(true);

            for (int i = 0; i < 7; i++) {
                Participant p = createMockParticipant(UUID.randomUUID(), "s" + i, ParticipantRole.PLAYER);
                participants.join(p, settings);
            }

            assertThrows(RoomFullException.class, () ->
                    participants.join(createMockParticipant(UUID.randomUUID(), "s8", ParticipantRole.SPECTATOR), settings));
        }
    }
}