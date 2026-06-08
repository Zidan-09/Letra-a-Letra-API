package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.StartGameInput;
import com.letraaletra.api.features.game.application.output.StartGameOutput;
import com.letraaletra.api.features.game.application.service.PickRandomThemeWordsService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.actor.command.StartGameActorCommand;
import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.board.service.BoardGenerator;
import com.letraaletra.api.features.game.domain.board.theme.Theme;
import com.letraaletra.api.features.game.domain.repository.ThemeRepository;
import com.letraaletra.api.features.game.domain.state.GameMode;
import com.letraaletra.api.features.game.domain.state.GameSettings;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.domain.security.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StartGameUseCaseTest {

    @Mock
    private ThemeRepository themeRepository;

    @Mock
    private PickRandomThemeWordsService pickRandomThemeWordsService;

    @Mock
    private BoardGenerator boardGenerator;

    @Mock
    private TokenService tokenService;

    @Mock
    private ActorManager<Game> gameActorManager;

    @Mock
    private Actor actor;

    @InjectMocks
    private StartGameUseCase useCase;

    @Test
    void shouldStartGameUsingThemeWords() {
        GameSettings settings = new GameSettings(
                "theme-id",
                mock(GameMode.class)
        );

        StartGameInput input = new StartGameInput(
                "token",
                "session",
                settings
        );

        Theme theme = mock(Theme.class);
        Board board = mock(Board.class);
        Game game = mock(Game.class);

        List<String> words = List.of(
                "word1",
                "word2",
                "word3",
                "word4",
                "word5"
        );

        when(tokenService.getTokenContent("token"))
                .thenReturn("game-id");

        when(themeRepository.findById("theme-id"))
                .thenReturn(theme);

        when(theme.pickRandomWords(eq(5), any()))
                .thenReturn(words);

        when(boardGenerator.generate(words, settings.getGameMode()))
                .thenReturn(board);

        when(gameActorManager.get("game-id"))
                .thenReturn(actor);

        when(actor.enqueueCommand(any(StartGameActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(game));

        StartGameOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals("game-id", output.id());
        assertEquals(game, output.game());

        verify(theme).pickRandomWords(eq(5), any());
        verify(pickRandomThemeWordsService, never()).execute();
    }

    @Test
    void shouldStartGameUsingRandomWordsWhenThemeDoesNotExist() {
        GameSettings settings = new GameSettings(
                "theme-id",
                mock(GameMode.class)
        );

        StartGameInput input = new StartGameInput(
                "token",
                "session",
                settings
        );

        Board board = mock(Board.class);
        Game game = mock(Game.class);

        List<String> words = List.of(
                "word1",
                "word2",
                "word3",
                "word4",
                "word5"
        );

        when(tokenService.getTokenContent("token"))
                .thenReturn("game-id");

        when(themeRepository.findById("theme-id"))
                .thenReturn(null);

        when(pickRandomThemeWordsService.execute())
                .thenReturn(words);

        when(boardGenerator.generate(words, settings.getGameMode()))
                .thenReturn(board);

        when(gameActorManager.get("game-id"))
                .thenReturn(actor);

        when(actor.enqueueCommand(any(StartGameActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(game));

        StartGameOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals("game-id", output.id());
        assertEquals(game, output.game());

        verify(pickRandomThemeWordsService).execute();
    }

    @Test
    void shouldGenerateBoardUsingSelectedWords() {
        GameMode gameMode = mock(GameMode.class);

        GameSettings settings = new GameSettings(
                "theme-id",
                gameMode
        );

        StartGameInput input = new StartGameInput(
                "token",
                "session",
                settings
        );

        Theme theme = mock(Theme.class);
        Board board = mock(Board.class);
        Game game = mock(Game.class);

        List<String> words = List.of(
                "word1",
                "word2",
                "word3",
                "word4",
                "word5"
        );

        when(tokenService.getTokenContent("token"))
                .thenReturn("game-id");

        when(themeRepository.findById("theme-id"))
                .thenReturn(theme);

        when(theme.pickRandomWords(eq(5), any()))
                .thenReturn(words);

        when(boardGenerator.generate(words, gameMode))
                .thenReturn(board);

        when(gameActorManager.get("game-id"))
                .thenReturn(actor);

        when(actor.enqueueCommand(any(StartGameActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(game));

        useCase.execute(input);

        verify(boardGenerator).generate(words, gameMode);
    }

    @Test
    void shouldSendStartGameCommandToActor() {
        GameSettings settings = new GameSettings(
                "theme-id",
                mock(GameMode.class)
        );

        StartGameInput input = new StartGameInput(
                "token",
                "session",
                settings
        );

        Theme theme = mock(Theme.class);
        Board board = mock(Board.class);
        Game game = mock(Game.class);

        when(tokenService.getTokenContent("token"))
                .thenReturn("game-id");

        when(themeRepository.findById("theme-id"))
                .thenReturn(theme);

        when(theme.pickRandomWords(eq(5), any()))
                .thenReturn(List.of("a", "b", "c", "d", "e"));

        when(boardGenerator.generate(anyList(), any()))
                .thenReturn(board);

        when(gameActorManager.get("game-id"))
                .thenReturn(actor);

        when(actor.enqueueCommand(any()))
                .thenReturn(CompletableFuture.completedFuture(game));

        useCase.execute(input);

        ArgumentCaptor<StartGameActorCommand> captor =
                ArgumentCaptor.forClass(StartGameActorCommand.class);

        verify(actor).enqueueCommand(captor.capture());

        assertNotNull(captor.getValue());
    }
}