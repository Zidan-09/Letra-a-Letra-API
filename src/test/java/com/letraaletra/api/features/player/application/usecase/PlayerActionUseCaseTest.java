package com.letraaletra.api.features.player.application.usecase;

import com.letraaletra.api.features.game.application.service.GameOverHandler;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.actor.command.PlayerActionActorCommand;
import com.letraaletra.api.features.game.domain.actor.output.PlayerActionResult;
import com.letraaletra.api.features.game.domain.event.Event;
import com.letraaletra.api.features.game.domain.service.GameOverResult;
import com.letraaletra.api.features.player.application.input.PlayerActionInput;
import com.letraaletra.api.features.player.application.output.PlayerActionOutput;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.power.domain.actions.GameAction;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.domain.security.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerActionUseCaseTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private ActorManager<Game> actorManager;

    @Mock
    private GameOverHandler gameOverHandler;

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

    @InjectMocks
    private PlayerActionUseCase playerActionUseCase;

    @Test
    @DisplayName("Deve executar ação do jogador com sucesso quando o jogo NÃO terminou")
    void shouldExecutePlayerActionSuccessfullyWhenGameIsNotOver() {
        String token = "valid-token";
        UUID gameId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        PlayerActionInput input = new PlayerActionInput(token, userId, mockGameAction);

        GameOverResult activeGameResult = new GameOverResult(false, null, null);
        List<Event> events = List.of(mockEvent);

        PlayerActionResult actionResult = new PlayerActionResult(events, activeGameResult, mockGame);

        when(tokenService.getTokenContent(token)).thenReturn(gameId);
        when(actorManager.get(gameId.toString())).thenReturn(actor);
        when(actor.enqueueCommand(any(PlayerActionActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(actionResult));

        PlayerActionOutput output = playerActionUseCase.execute(input);

        assertNotNull(output);
        assertEquals(mockGame, output.game());
        assertEquals(events, output.events());
        assertTrue(output.gameOver().isEmpty(), "O Optional 'gameOver' deveria estar vazio");

        verify(gameOverHandler, times(1)).handle(mockGame, activeGameResult);

        ArgumentCaptor<PlayerActionActorCommand> commandCaptor = ArgumentCaptor.forClass(PlayerActionActorCommand.class);
        verify(actor).enqueueCommand(commandCaptor.capture());

        PlayerActionActorCommand capturedCommand = commandCaptor.getValue();

        assertNotNull(capturedCommand);
    }

    @Test
    @DisplayName("Deve retornar o GameOverResult popular quando a ação causar o FIM do jogo")
    void shouldReturnGameOverResultWhenActionFinishesTheGame() {
        String token = "valid-token";
        UUID gameId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        PlayerActionInput input = new PlayerActionInput(token, userId, mockGameAction);

        GameOverResult finishedGameResult = new GameOverResult(true, mockPlayer, mockPlayer);
        List<Event> events = List.of();
        PlayerActionResult actionResult = new PlayerActionResult(events, finishedGameResult, mockGame);

        when(tokenService.getTokenContent(token)).thenReturn(gameId);
        when(actorManager.get(gameId.toString())).thenReturn(actor);
        when(actor.enqueueCommand(any(PlayerActionActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(actionResult));

        PlayerActionOutput output = playerActionUseCase.execute(input);

        assertNotNull(output);
        assertTrue(output.gameOver().isPresent(), "O Optional 'gameOver' deveria conter o resultado");
        assertEquals(finishedGameResult, output.gameOver().get());

        verify(gameOverHandler).handle(mockGame, finishedGameResult);
    }
}