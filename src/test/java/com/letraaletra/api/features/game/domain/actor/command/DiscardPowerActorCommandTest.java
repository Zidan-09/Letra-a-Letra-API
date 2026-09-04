package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.board.power.PowerType;
import com.letraaletra.api.features.game.domain.event.StateEvent;
import com.letraaletra.api.features.game.domain.room.RoomSettings;
import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.game.domain.turn.port.TurnTimeoutManager;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.player.domain.effect.FreezeEffect;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.inventory.Inventory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscardPowerActorCommandTest {

    @Mock
    private Board mockBoard;

    @Mock
    private TurnTimeoutManager turnTimeoutManager;

    private Game game;
    private GameState state;
    private Player playerOnTurn;
    private Player otherPlayer;
    private UUID playerOnTurnId;
    private UUID otherPlayerId;

    private void initGame() {
        lenient().when(mockBoard.getCell(any())).thenAnswer(inv -> null);
        game = Game.create("CODE", "room", new RoomSettings(true, false), GameType.CUSTOM);
        Inventory inv1 = mock(Inventory.class);
        Inventory inv2 = mock(Inventory.class);
        lenient().when(inv1.getItems()).thenReturn(Collections.emptyList());
        lenient().when(inv2.getItems()).thenReturn(Collections.emptyList());
        User u1 = mock(User.class);
        User u2 = mock(User.class);
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        lenient().when(u1.getUserId()).thenReturn(id1);
        lenient().when(u2.getUserId()).thenReturn(id2);
        lenient().when(u1.getUsername()).thenReturn("p1");
        lenient().when(u2.getUsername()).thenReturn("p2");
        lenient().when(u1.getInventory()).thenReturn(inv1);
        lenient().when(u2.getInventory()).thenReturn(inv2);
        game.join(u1, "s1");
        game.join(u2, "s2");
        Map<UUID, Player> players = new LinkedHashMap<>();
        for (Participant p : game.getParticipants().getParticipants()) {
            if (p.isPlayer()) players.put(p.getUserId(), new Player(p.getUserId(), p.getNickname()));
        }
        state = new GameState(UUID.randomUUID(), players, mockBoard, Instant.now().plusSeconds(45));
        game.updateGameState(state);
        game.setGameStatus(GameStatus.RUNNING);
        UUID cur = state.currentPlayerTurn();
        playerOnTurnId = cur;
        playerOnTurn = state.getPlayerOrThrow(cur);
        otherPlayerId = players.keySet().stream().filter(id -> !id.equals(cur)).findFirst().orElseThrow();
        otherPlayer = state.getPlayerOrThrow(otherPlayerId);
    }

    private String addPower(Player p, PowerType t) {
        p.addToInventory(t);
        return p.getInventory().entrySet().stream().filter(e -> e.getValue() == t).map(Map.Entry::getKey).findFirst().orElseThrow();
    }

    @Test
    @DisplayName("Deve permitir descarte mesmo quando congelado")
    void shouldAllowDiscardWhenFrozen() {
        initGame();
        playerOnTurn.applyEffect(new FreezeEffect());
        String id = addPower(playerOnTurn, PowerType.BLOCK);
        DiscardPowerActorCommand cmd = new DiscardPowerActorCommand(playerOnTurnId, id, turnTimeoutManager);
        assertDoesNotThrow(() -> cmd.execute(game));
        assertFalse(playerOnTurn.getInventory().containsKey(id));
    }

    @Test
    @DisplayName("Deve passar turno quando congelado descarta última defesa (UNFREEZE) estando em sua vez")
    void shouldPassTurnWhenFrozenDiscardsLastDefenseOnTurn() {
        initGame();
        playerOnTurn.applyEffect(new FreezeEffect());
        String unfreezeId = addPower(playerOnTurn, PowerType.UNFREEZE);
        // ensure it's the only defense
        assertTrue(playerOnTurn.hasFreezeDefense());
        DiscardPowerActorCommand cmd = new DiscardPowerActorCommand(playerOnTurnId, unfreezeId, turnTimeoutManager);
        var result = cmd.execute(game);
        assertFalse(playerOnTurn.hasFreezeDefense());
        assertTrue(playerOnTurn.isFrozen());
        assertTrue(playerOnTurn.canNotPlay());
        // turno deve ter passado para o outro jogador
        assertEquals(otherPlayerId, state.currentPlayerTurn());
        assertTrue(result.events().stream().anyMatch(e -> e.event() == StateEvent.TURN_PASSED));
        verify(turnTimeoutManager).start(game);
    }

    @Test
    @DisplayName("Não deve passar turno se ainda resta outra defesa após descarte")
    void shouldNotPassIfStillHasDefense() {
        initGame();
        playerOnTurn.applyEffect(new FreezeEffect());
        String unfreezeId = addPower(playerOnTurn, PowerType.UNFREEZE);
        addPower(playerOnTurn, PowerType.IMMUNITY);
        DiscardPowerActorCommand cmd = new DiscardPowerActorCommand(playerOnTurnId, unfreezeId, turnTimeoutManager);
        var result = cmd.execute(game);
        assertTrue(playerOnTurn.hasFreezeDefense());
        assertFalse(playerOnTurn.canNotPlay());
        assertEquals(playerOnTurnId, state.currentPlayerTurn(), "Turno não deve passar pois ainda tem IMMUNITY");
        assertTrue(result.events().isEmpty());
        verify(turnTimeoutManager, never()).start(any());
    }

    @Test
    @DisplayName("Não deve passar turno se descarte ocorre fora da vez do jogador congelado")
    void shouldNotPassIfNotOnTurn() {
        initGame();
        // otherPlayer is frozen, but it's playerOnTurn's turn; otherPlayer discards
        otherPlayer.applyEffect(new FreezeEffect());
        String id = addPower(otherPlayer, PowerType.UNFREEZE);
        DiscardPowerActorCommand cmd = new DiscardPowerActorCommand(otherPlayerId, id, turnTimeoutManager);
        var result = cmd.execute(game);
        // otherPlayer now frozen without defense but it's not his turn, so no immediate pass
        assertEquals(playerOnTurnId, state.currentPlayerTurn());
        assertTrue(result.events().isEmpty());
    }

    @Test
    @DisplayName("Deve permitir descarte de poder não defensivo quando congelado sem passar turno se ainda tem defesa")
    void shouldAllowDiscardNonDefenseWhenFrozenWithDefense() {
        initGame();
        playerOnTurn.applyEffect(new FreezeEffect());
        addPower(playerOnTurn, PowerType.UNFREEZE);
        String blockId = addPower(playerOnTurn, PowerType.BLOCK);
        DiscardPowerActorCommand cmd = new DiscardPowerActorCommand(playerOnTurnId, blockId, turnTimeoutManager);
        var result = cmd.execute(game);
        assertTrue(playerOnTurn.hasFreezeDefense());
        assertEquals(playerOnTurnId, state.currentPlayerTurn());
        assertTrue(result.events().isEmpty());
    }

    @Test
    @DisplayName("Descarte deve falhar se powerId não existe")
    void shouldFailIfPowerNotFound() {
        initGame();
        DiscardPowerActorCommand cmd = new DiscardPowerActorCommand(playerOnTurnId, "invalid", turnTimeoutManager);
        assertThrows(com.letraaletra.api.features.player.domain.exception.InvalidPlayerActionException.class, () -> cmd.execute(game));
    }
}
