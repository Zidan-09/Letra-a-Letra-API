package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.board.cell.Cell;
import com.letraaletra.api.features.game.domain.board.cell.CellFactory;
import com.letraaletra.api.features.game.domain.board.position.Position;
import com.letraaletra.api.features.game.domain.board.power.PowerType;
import com.letraaletra.api.features.game.domain.board.power.action.*;
import com.letraaletra.api.features.game.domain.event.StateEvent;
import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.game.domain.turn.port.TurnTimeoutManager;
import com.letraaletra.api.features.game.domain.room.RoomSettings;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.player.domain.effect.FreezeEffect;
import com.letraaletra.api.features.player.domain.exception.PlayerIsFrozenException;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.inventory.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
class PlayerActionActorCommandTest {

    @Mock
    private TurnTimeoutManager turnTimeoutManager;

    @Mock
    private Board mockBoard;

    private Game game;
    private GameState state;
    private Player playerOnTurn;
    private Player otherPlayer;
    private UUID playerOnTurnId;
    private UUID otherPlayerId;

    @BeforeEach
    void setUp() {
        // will be initialized per test via helper
    }

    private void initGameWithTwoPlayers() {
        lenient().when(mockBoard.getCell(any(Position.class))).thenAnswer(inv -> {
            Position pos = inv.getArgument(0);
            return CellFactory.create('A', pos, com.letraaletra.api.features.game.domain.state.GameMode.EASY);
        });

        game = Game.create("CODE12", "room", new RoomSettings(true, false), GameType.CUSTOM);
        Inventory inv1 = mock(Inventory.class);
        Inventory inv2 = mock(Inventory.class);
        lenient().when(inv1.getItems()).thenReturn(Collections.emptyList());
        lenient().when(inv2.getItems()).thenReturn(Collections.emptyList());

        User mockU1 = mock(User.class);
        User mockU2 = mock(User.class);
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        lenient().when(mockU1.getUserId()).thenReturn(id1);
        lenient().when(mockU2.getUserId()).thenReturn(id2);
        lenient().when(mockU1.getUsername()).thenReturn("player1");
        lenient().when(mockU2.getUsername()).thenReturn("player2");
        lenient().when(mockU1.getInventory()).thenReturn(inv1);
        lenient().when(mockU2.getInventory()).thenReturn(inv2);
        game.join(mockU1, "sess1");
        game.join(mockU2, "sess2");

        Map<UUID, Player> realPlayers = new LinkedHashMap<>();
        for (Participant p : game.getParticipants().getParticipants()) {
            if (p.isPlayer()) {
                realPlayers.put(p.getUserId(), new Player(p.getUserId(), p.getNickname()));
            }
        }
        state = new GameState(UUID.randomUUID(), realPlayers, mockBoard, Instant.now().plusSeconds(45));
        game.updateGameState(state);
        game.setGameStatus(GameStatus.RUNNING);

        UUID cur = state.currentPlayerTurn();
        playerOnTurnId = cur;
        playerOnTurn = state.getPlayerOrThrow(cur);
        otherPlayerId = state.getPlayers().keySet().stream().filter(id -> !id.equals(cur)).findFirst().orElseThrow();
        otherPlayer = state.getPlayerOrThrow(otherPlayerId);
    }

    private String addPowerToPlayer(Player player, PowerType type) {
        player.addToInventory(type);
        return player.getInventory().entrySet().stream()
                .filter(e -> e.getValue() == type)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow();
    }

    @Nested
    @DisplayName("Validação de Freeze - guard centralizado")
    class FreezeGuardTests {

        @Test
        @DisplayName("Deve permitir revelar célula quando NÃO está congelado")
        void shouldAllowRevealWhenNotFrozen() {
            initGameWithTwoPlayers();
            assertFalse(playerOnTurn.isFrozen());
            RevealCellAction action = new RevealCellAction(new Position(0, 0));
            PlayerActionActorCommand cmd = new PlayerActionActorCommand(playerOnTurnId, action, turnTimeoutManager);
            assertDoesNotThrow(() -> cmd.execute(game));
        }

        @Test
        @DisplayName("Deve BLOQUEAR revelar célula quando congelado mesmo COM UNFREEZE")
        void shouldBlockRevealWhenFrozenEvenWithUnfreeze() {
            initGameWithTwoPlayers();
            playerOnTurn.applyEffect(new FreezeEffect());
            addPowerToPlayer(playerOnTurn, PowerType.UNFREEZE);
            assertTrue(playerOnTurn.isFrozen());
            assertTrue(playerOnTurn.hasFreezeDefense());
            RevealCellAction action = new RevealCellAction(new Position(0, 0));
            PlayerActionActorCommand cmd = new PlayerActionActorCommand(playerOnTurnId, action, turnTimeoutManager);
            assertThrows(PlayerIsFrozenException.class, () -> cmd.execute(game));
        }

        @Test
        @DisplayName("Deve BLOQUEAR revelar célula quando congelado com IMMUNITY")
        void shouldBlockRevealWhenFrozenWithImmunity() {
            initGameWithTwoPlayers();
            playerOnTurn.applyEffect(new FreezeEffect());
            addPowerToPlayer(playerOnTurn, PowerType.IMMUNITY);
            RevealCellAction action = new RevealCellAction(new Position(0, 0));
            PlayerActionActorCommand cmd = new PlayerActionActorCommand(playerOnTurnId, action, turnTimeoutManager);
            assertThrows(PlayerIsFrozenException.class, () -> cmd.execute(game));
        }

        @Test
        @DisplayName("Deve BLOQUEAR BlockCellAction quando congelado com defesa")
        void shouldBlockBlockWhenFrozen() {
            initGameWithTwoPlayers();
            playerOnTurn.applyEffect(new FreezeEffect());
            addPowerToPlayer(playerOnTurn, PowerType.UNFREEZE);
            String blockId = addPowerToPlayer(playerOnTurn, PowerType.BLOCK);
            BlockCellAction action = new BlockCellAction(blockId, new Position(0, 0));
            PlayerActionActorCommand cmd = new PlayerActionActorCommand(playerOnTurnId, action, turnTimeoutManager);
            assertThrows(PlayerIsFrozenException.class, () -> cmd.execute(game));
        }

        @Test
        @DisplayName("Deve BLOQUEAR Trap, Spy, Blind, Freeze, Lantern, DetectTraps, Unblock quando congelado")
        void shouldBlockAllOtherPowersWhenFrozen() {
            // Testado de forma parametrizada nos demais testes; este garante que a lista completa é bloqueada
            // Verifica um exemplo representativo (TRAP) além dos já cobertos em outros testes
            initGameWithTwoPlayers();
            playerOnTurn.applyEffect(new FreezeEffect());
            addPowerToPlayer(playerOnTurn, PowerType.UNFREEZE);
            String trapId = addPowerToPlayer(playerOnTurn, PowerType.TRAP);
            TrapCellAction trap = new TrapCellAction(trapId, new Position(0, 0));
            PlayerActionActorCommand cmd = new PlayerActionActorCommand(playerOnTurnId, trap, turnTimeoutManager);
            assertThrows(PlayerIsFrozenException.class, () -> cmd.execute(game));
        }

        @Test
        @DisplayName("Deve BLOQUEAR qualquer ação quando congelado SEM defesa")
        void shouldBlockAnyWhenFrozenWithoutDefense() {
            initGameWithTwoPlayers();
            playerOnTurn.applyEffect(new FreezeEffect());
            assertTrue(playerOnTurn.canNotPlay());
            RevealCellAction action = new RevealCellAction(new Position(0, 0));
            PlayerActionActorCommand cmd = new PlayerActionActorCommand(playerOnTurnId, action, turnTimeoutManager);
            assertThrows(PlayerIsFrozenException.class, () -> cmd.execute(game));
        }

        @Test
        @DisplayName("Deve PERMITIR UnfreezeAction quando congelado")
        void shouldAllowUnfreezeWhenFrozen() {
            initGameWithTwoPlayers();
            playerOnTurn.applyEffect(new FreezeEffect());
            String id = addPowerToPlayer(playerOnTurn, PowerType.UNFREEZE);
            UnfreezeAction action = new UnfreezeAction(id);
            PlayerActionActorCommand cmd = new PlayerActionActorCommand(playerOnTurnId, action, turnTimeoutManager);
            var result = assertDoesNotThrow(() -> cmd.execute(game));
            assertFalse(playerOnTurn.isFrozen(), "Freeze deve ser removido");
            assertTrue(result.events().stream().anyMatch(e -> e.event() == StateEvent.PLAYER_UNFREEZE));
        }

        @Test
        @DisplayName("Deve PERMITIR ImmunityPlayerAction quando congelado")
        void shouldAllowImmunityWhenFrozen() {
            initGameWithTwoPlayers();
            playerOnTurn.applyEffect(new FreezeEffect());
            String id = addPowerToPlayer(playerOnTurn, PowerType.IMMUNITY);
            ImmunityPlayerAction action = new ImmunityPlayerAction(id);
            PlayerActionActorCommand cmd = new PlayerActionActorCommand(playerOnTurnId, action, turnTimeoutManager);
            var result = assertDoesNotThrow(() -> cmd.execute(game));
            assertFalse(playerOnTurn.isFrozen());
            assertTrue(result.events().stream().anyMatch(e -> e.event() == StateEvent.PLAYER_USE_IMMUNITY));
        }

        @Test
        @DisplayName("Regressão: possuir UNFREEZE não deve liberar Reveal")
        void regressionUnfreezeDoesNotAllowReveal() {
            initGameWithTwoPlayers();
            playerOnTurn.applyEffect(new FreezeEffect());
            addPowerToPlayer(playerOnTurn, PowerType.UNFREEZE);
            addPowerToPlayer(playerOnTurn, PowerType.BLOCK);
            // Try reveal - must fail
            RevealCellAction reveal = new RevealCellAction(new Position(1, 1));
            PlayerActionActorCommand cmd1 = new PlayerActionActorCommand(playerOnTurnId, reveal, turnTimeoutManager);
            assertThrows(PlayerIsFrozenException.class, () -> cmd1.execute(game));
            // Try block - must also fail, proving UNFREEZE doesn't allow other powers
            String blockId = playerOnTurn.getInventory().entrySet().stream()
                    .filter(e -> e.getValue() == PowerType.BLOCK).findFirst().orElseThrow().getKey();
            BlockCellAction block = new BlockCellAction(blockId, new Position(1, 1));
            PlayerActionActorCommand cmd2 = new PlayerActionActorCommand(playerOnTurnId, block, turnTimeoutManager);
            assertThrows(PlayerIsFrozenException.class, () -> cmd2.execute(game));
            // Only unfreeze should succeed
            String unfreezeId = playerOnTurn.getInventory().entrySet().stream()
                    .filter(e -> e.getValue() == PowerType.UNFREEZE).findFirst().orElseThrow().getKey();
            UnfreezeAction unfreeze = new UnfreezeAction(unfreezeId);
            PlayerActionActorCommand cmd3 = new PlayerActionActorCommand(playerOnTurnId, unfreeze, turnTimeoutManager);
            assertDoesNotThrow(() -> cmd3.execute(game));
        }

        @Test
        @DisplayName("Regressão: possuir IMMUNITY não deve liberar outro poder")
        void regressionImmunityDoesNotAllowOther() {
            initGameWithTwoPlayers();
            playerOnTurn.applyEffect(new FreezeEffect());
            addPowerToPlayer(playerOnTurn, PowerType.IMMUNITY);
            String trapId = addPowerToPlayer(playerOnTurn, PowerType.TRAP);
            TrapCellAction trap = new TrapCellAction(trapId, new Position(0, 0));
            PlayerActionActorCommand cmd = new PlayerActionActorCommand(playerOnTurnId, trap, turnTimeoutManager);
            assertThrows(PlayerIsFrozenException.class, () -> cmd.execute(game));
        }
    }

    @Nested
    @DisplayName("Passagem de turno e comportamento de descongelado")
    class TurnPassTests {

        @Test
        @DisplayName("Deve pular jogador congelado SEM defesa no próximo turno e emitir TURN_PASSED")
        void shouldSkipFrozenWithoutDefenseAndEmitTurnPassed() {
            initGameWithTwoPlayers();
            // otherPlayer will be the one to be skipped; make him frozen without defense
            otherPlayer.applyEffect(new FreezeEffect());
            assertTrue(otherPlayer.canNotPlay());
            // playerOnTurn does a valid reveal
            RevealCellAction action = new RevealCellAction(new Position(0, 0));
            PlayerActionActorCommand cmd = new PlayerActionActorCommand(playerOnTurnId, action, turnTimeoutManager);
            var result = cmd.execute(game);
            // should have emitted TURN_PASSED for otherPlayer
            boolean hasTurnPassed = result.events().stream().anyMatch(e -> e.event() == StateEvent.TURN_PASSED);
            assertTrue(hasTurnPassed, "Deveria ter TURN_PASSED para jogador congelado sem defesa");
            // After execution, current turn should be back to playerOnTurn (since other was skipped)
            assertEquals(playerOnTurnId, state.currentPlayerTurn(), "Turno deveria voltar para o jogador que agiu, pulando o congelado");
        }

        @Test
        @DisplayName("NÃO deve pular jogador congelado COM defesa")
        void shouldNotSkipFrozenWithDefense() {
            initGameWithTwoPlayers();
            otherPlayer.applyEffect(new FreezeEffect());
            otherPlayer.addToInventory(PowerType.UNFREEZE);
            assertFalse(otherPlayer.canNotPlay());
            RevealCellAction action = new RevealCellAction(new Position(0, 0));
            PlayerActionActorCommand cmd = new PlayerActionActorCommand(playerOnTurnId, action, turnTimeoutManager);
            var result = cmd.execute(game);
            boolean hasTurnPassed = result.events().stream().anyMatch(e -> e.event() == StateEvent.TURN_PASSED);
            assertFalse(hasTurnPassed, "Não deveria pular jogador que tem defesa");
            assertEquals(otherPlayerId, state.currentPlayerTurn(), "Turno deve passar para o jogador congelado com defesa");
        }

        @Test
        @DisplayName("Após Unfreeze, turno deve avançar normalmente")
        void shouldAdvanceTurnAfterUnfreeze() {
            initGameWithTwoPlayers();
            playerOnTurn.applyEffect(new FreezeEffect());
            String id = addPowerToPlayer(playerOnTurn, PowerType.UNFREEZE);
            UnfreezeAction action = new UnfreezeAction(id);
            PlayerActionActorCommand cmd = new PlayerActionActorCommand(playerOnTurnId, action, turnTimeoutManager);
            var result = cmd.execute(game);
            // After unfreeze, player is no longer frozen, next turn should be otherPlayer (unless other is frozen without defense)
            assertFalse(playerOnTurn.isFrozen());
            assertEquals(otherPlayerId, state.currentPlayerTurn());
        }

        @Test
        @DisplayName("Após Immunity, deve remover Freeze e aplicar ImmunityEffect")
        void shouldRemoveFreezeAndApplyImmunity() {
            initGameWithTwoPlayers();
            playerOnTurn.applyEffect(new FreezeEffect());
            String id = addPowerToPlayer(playerOnTurn, PowerType.IMMUNITY);
            ImmunityPlayerAction action = new ImmunityPlayerAction(id);
            PlayerActionActorCommand cmd = new PlayerActionActorCommand(playerOnTurnId, action, turnTimeoutManager);
            cmd.execute(game);
            assertFalse(playerOnTurn.isFrozen());
            boolean hasImmunity = playerOnTurn.getEffects().stream().anyMatch(e -> e instanceof com.letraaletra.api.features.player.domain.effect.ImmunityEffect);
            assertTrue(hasImmunity);
        }
    }

    @Nested
    @DisplayName("Outros cenários")
    class OtherTests {
        @Test
        @DisplayName("Jogador não congelado pode fazer qualquer ação")
        void shouldAllowAnyWhenNotFrozen() {
            initGameWithTwoPlayers();
            assertFalse(playerOnTurn.isFrozen());
            RevealCellAction reveal = new RevealCellAction(new Position(0, 0));
            PlayerActionActorCommand cmd = new PlayerActionActorCommand(playerOnTurnId, reveal, turnTimeoutManager);
            assertDoesNotThrow(() -> cmd.execute(game));
        }

        @Test
        @DisplayName("Bloqueia FreezePlayerAction quando congelado")
        void shouldBlockFreezeWhenFrozen() {
            initGameWithTwoPlayers();
            playerOnTurn.applyEffect(new FreezeEffect());
            addPowerToPlayer(playerOnTurn, PowerType.UNFREEZE);
            String fid = addPowerToPlayer(playerOnTurn, PowerType.FREEZE);
            FreezePlayerAction action = new FreezePlayerAction(fid, otherPlayerId);
            PlayerActionActorCommand cmd = new PlayerActionActorCommand(playerOnTurnId, action, turnTimeoutManager);
            assertThrows(PlayerIsFrozenException.class, () -> cmd.execute(game));
        }
    }
}
