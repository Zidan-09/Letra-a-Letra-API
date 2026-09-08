package com.letraaletra.api.features.player.application.usecase;

import com.letraaletra.api.features.game.application.output.HandledGameOver;
import com.letraaletra.api.features.game.application.port.Actor;
import com.letraaletra.api.features.game.application.port.ActorManager;
import com.letraaletra.api.features.game.application.port.GameOverFinalizer;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameOver;
import com.letraaletra.api.features.game.domain.GameOverReasons;
import com.letraaletra.api.features.game.domain.actor.command.PlayerActionActorCommand;
import com.letraaletra.api.features.game.domain.actor.result.PlayerActionResult;
import com.letraaletra.api.features.game.domain.board.power.action.GameAction;
import com.letraaletra.api.features.game.domain.event.Event;
import com.letraaletra.api.features.game.domain.turn.port.TurnTimeoutManager;
import com.letraaletra.api.features.player.application.input.PlayerActionInput;
import com.letraaletra.api.features.player.application.output.PlayerActionOutput;
import com.letraaletra.api.features.player.domain.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerActionUseCaseTest {

    @Mock
    private TurnTimeoutManager turnTimeoutManager;

    @Mock
    private ActorManager<Game> gameActorManager;

    @Mock
    private GameOverFinalizer gameOverFinalizer;

    @Mock
    private Actor actor;

    @Mock
    private Game mockGame;

    @Mock
    private GameAction mockGameAction;

    @Mock
    private Event mockEvent;

    @Mock
    private Player mockPlayer;

    private final UUID userId = UUID.randomUUID();

    @InjectMocks
    private PlayerActionUseCase playerActionUseCase;

    @Test
    @DisplayName("Deve executar ação do jogador com sucesso quando o jogo NÃO terminou")
    void shouldExecutePlayerActionSuccessfullyWhenGameIsNotOver() {
        UUID gameId = UUID.randomUUID();
        PlayerActionInput input = new PlayerActionInput(gameId.toString(), userId, mockGameAction);
        List<Event> events = List.of(mockEvent);

        PlayerActionResult actionResult = new PlayerActionResult(
                events,
                Optional.empty(),
                mockGame
        );

        when(gameActorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(PlayerActionActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(actionResult));

        PlayerActionOutput output = playerActionUseCase.execute(input);

        assertNotNull(output);
        assertEquals(mockGame, output.game());
        assertEquals(events, output.events());
        assertTrue(output.gameOver().isEmpty(), "O Optional 'gameOver' deveria estar vazio");
        assertEquals(HandledGameOver.withoutRanking(), output.handledGameOver());

        verifyNoInteractions(gameOverFinalizer);

        ArgumentCaptor<PlayerActionActorCommand> commandCaptor = ArgumentCaptor.forClass(PlayerActionActorCommand.class);
        verify(actor).enqueueCommand(commandCaptor.capture());

        PlayerActionActorCommand capturedCommand = commandCaptor.getValue();
        assertNotNull(capturedCommand);
    }

    @Test
    @DisplayName("Deve finalizar o jogo via GameOverFinalizer quando a ação causar o FIM do jogo")
    void shouldReturnGameOverResultWhenActionFinishesTheGame() {
        UUID gameId = UUID.randomUUID();
        PlayerActionInput input = new PlayerActionInput(gameId.toString(), userId, mockGameAction);

        GameOver finishedGameResult = new GameOver(
                GameOverReasons.SCORE,
                mockPlayer,
                mockPlayer
        );

        HandledGameOver handledGameOver = mock(HandledGameOver.class);

        List<Event> events = List.of();
        PlayerActionResult actionResult = new PlayerActionResult(
                events,
                Optional.of(finishedGameResult),
                mockGame
        );

        when(gameActorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(PlayerActionActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(actionResult));
        when(gameOverFinalizer.finish(mockGame, finishedGameResult)).thenReturn(handledGameOver);

        PlayerActionOutput output = playerActionUseCase.execute(input);

        assertNotNull(output);
        assertTrue(output.gameOver().isPresent(), "O Optional 'gameOver' deveria conter o resultado");
        assertEquals(finishedGameResult, output.gameOver().get());
        assertEquals(handledGameOver, output.handledGameOver());

        verify(gameOverFinalizer).finish(mockGame, finishedGameResult);
    }
}