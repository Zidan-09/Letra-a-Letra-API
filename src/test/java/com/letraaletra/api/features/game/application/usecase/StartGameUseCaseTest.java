package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.StartGameInput;
import com.letraaletra.api.features.game.application.output.StartGameOutput;
import com.letraaletra.api.features.game.application.port.SelectThemeService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.actor.command.StartCustomGameActorCommand;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.game.domain.service.GameTimeoutManager;
import com.letraaletra.api.features.game.domain.service.TurnTimeoutManager;
import com.letraaletra.api.features.game.domain.state.GameMode;
import com.letraaletra.api.features.game.domain.state.GameSettings;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import org.junit.jupiter.api.BeforeEach;
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
class StartGameUseCaseTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameTimeoutManager gameTimeoutManager;

    @Mock
    private SelectThemeService themeService;

    @Mock
    private TurnTimeoutManager turnTimeoutManager;

    @Mock
    private ActorManager<Game> gameActorManager;

    @Mock
    private Actor actor;

    @InjectMocks
    private StartGameUseCase useCase;

    private UUID gameId;
    private StartGameInput input;

    @BeforeEach
    void setup() {
        GameSettings settings = new GameSettings(
                "theme-id",
                GameMode.NORMAL
        );

        gameId = UUID.randomUUID();

        input = new StartGameInput(
                gameId,
                "session-123",
                settings
        );
    }

    @Test
    @DisplayName("Deve iniciar o jogo com sucesso quando o input for válido")
    void shouldStartGameSuccessfully() {
        List<String> words = List.of("palavra1", "palavra2", "palavra3");
        Game game = mock(Game.class);

        when(themeService.select("theme-id")).thenReturn(words);
        when(gameActorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(StartCustomGameActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(game));

        StartGameOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(game, output.game());

        verify(themeService).select("theme-id");
        verify(gameRepository).save(game);
    }

    @Test
    @DisplayName("Deve enviar o comando StartCustomGameActorCommand correto para o Actor")
    void shouldSendStartGameCommandToActor() {
        List<String> words = List.of("palavra1", "palavra2");
        Game game = mock(Game.class);

        when(themeService.select("theme-id")).thenReturn(words);
        when(gameActorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(StartCustomGameActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(game));

        useCase.execute(input);

        ArgumentCaptor<StartCustomGameActorCommand> captor =
                ArgumentCaptor.forClass(StartCustomGameActorCommand.class);

        verify(actor).enqueueCommand(captor.capture());

        StartCustomGameActorCommand capturedCommand = captor.getValue();
        assertNotNull(capturedCommand);
    }
}