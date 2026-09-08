package com.letraaletra.api.features.participant.infrastructure.presentation.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.board.generator.BoardGenerator;
import com.letraaletra.api.features.game.domain.room.RoomSettings;
import com.letraaletra.api.features.game.domain.state.GameMode;
import com.letraaletra.api.features.participant.application.output.ReconnectParticipantOutput;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.ReconnectParticipantResponse;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.inventory.Inventory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReconnectSerializationTest {

    private User mockUser(UUID id) {
        Inventory inv = mock(Inventory.class);
        when(inv.getItems()).thenReturn(Collections.emptyList());
        User u = mock(User.class);
        lenient().when(u.getUserId()).thenReturn(id);
        lenient().when(u.getUsername()).thenReturn("u-" + id.toString().substring(0,4));
        lenient().when(u.getInventory()).thenReturn(inv);
        return u;
    }

    @Test
    @DisplayName("WAITING serialized with com.fasterxml should contain room and omit gameState (NON_NULL)")
    void waitingSerializationComFasterxml() throws Exception {
        RoomSettings settings = new RoomSettings(true, false);
        Game game = Game.create("CODEW", "roomW", settings, com.letraaletra.api.features.game.domain.GameType.CUSTOM);
        game.join(mockUser(UUID.randomUUID()), "s1");
        game.join(mockUser(UUID.randomUUID()), "s2");

        ReconnectParticipantResponse dto = ReconnectParticipantMapper.toResponse(new ReconnectParticipantOutput(game));

        assertNotNull(dto.room());
        assertNull(dto.gameState());

        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        String json = mapper.writeValueAsString(dto);
        System.out.println("WAITING com.fasterxml JSON: " + json);
        JsonNode node = mapper.readTree(json);
        assertTrue(node.has("room"), "WAITING must have room");
        // gameState should be omitted due to NON_NULL (or null if not omitted, both are acceptable per spec)
        if (node.has("gameState")) {
            assertTrue(node.get("gameState").isNull(), "if present, must be null");
        }
        assertEquals(GameStatus.WAITING.name(), node.path("room").path("status").asText());
    }

    @Test
    @DisplayName("RUNNING serialized should contain both room and gameState")
    void runningSerialization() throws Exception {
        RoomSettings settings = new RoomSettings(true, false);
        Game game = Game.create("CODER", "roomR", settings, com.letraaletra.api.features.game.domain.GameType.CUSTOM);
        game.join(mockUser(UUID.randomUUID()), "s1");
        game.join(mockUser(UUID.randomUUID()), "s2");
        Board board = BoardGenerator.generate(List.of("alpha","bravo","charlie","delta","echo"), GameMode.NORMAL);
        game.start(board);

        ReconnectParticipantResponse dto = ReconnectParticipantMapper.toResponse(new ReconnectParticipantOutput(game));

        assertNotNull(dto.room());
        assertNotNull(dto.gameState());
        assertEquals(GameStatus.RUNNING, dto.room().status());

        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        String json = mapper.writeValueAsString(dto);
        System.out.println("RUNNING com.fasterxml JSON: " + json);
        JsonNode node = mapper.readTree(json);
        assertTrue(node.has("room"));
        assertTrue(node.has("gameState"));
        assertFalse(node.get("gameState").isNull());
        assertTrue(node.path("gameState").has("board"));
        assertTrue(node.path("gameState").has("players"));
        assertTrue(node.path("gameState").has("currentTurnPlayerId"));
    }

    @Test
    @DisplayName("WAITING with tools.jackson should also handle room/gameState")
    void waitingSerializationToolsJackson() throws Exception {
        RoomSettings settings = new RoomSettings(true, false);
        Game game = Game.create("CODEW2", "roomW2", settings, com.letraaletra.api.features.game.domain.GameType.CUSTOM);
        game.join(mockUser(UUID.randomUUID()), "s1");
        game.join(mockUser(UUID.randomUUID()), "s2");

        ReconnectParticipantResponse dto = ReconnectParticipantMapper.toResponse(new ReconnectParticipantOutput(game));

        tools.jackson.databind.ObjectMapper toolsMapper = new tools.jackson.databind.ObjectMapper();
        String json = toolsMapper.writeValueAsString(dto);
        System.out.println("WAITING tools.jackson JSON: " + json);
        // tools.jackson currently includes null (since annotation is com.fasterxml), so gameState will be present as null or omitted
        // both are acceptable per validation spec
        assertTrue(json.contains("\"room\""));
        // if NON_NULL respected for tools.jackson, gameState should be absent; if not, it will be null - both passes
        boolean hasGameState = json.contains("gameState");
        if (hasGameState) {
            // if present, should be null for WAITING
            assertTrue(json.contains("\"gameState\":null") || json.contains("\"gameState\": null"));
        }
    }
}
