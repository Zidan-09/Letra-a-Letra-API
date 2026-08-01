package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.LeftGameInput;
import com.letraaletra.api.features.game.application.output.LeftGameOutput;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.actor.command.LeftGameActorCommand;
import com.letraaletra.api.features.game.domain.actor.output.LeftGameResult;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.game.domain.service.GameOver;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeftGameUseCaseTest {

    @Mock
    private ActorManager<Game> actorManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private Actor actor;

    @InjectMocks
    private LeftGameUseCase useCase;

    private UUID gameId;
    private UUID userId;
    private String session;
    private LeftGameInput input;

    @BeforeEach
    void setup() {
        gameId = UUID.randomUUID();
        userId = UUID.randomUUID();
        session = "session-123";
        input = new LeftGameInput(gameId, session);
    }

    @Test
    @DisplayName("Deve remover jogador da partida com sucesso quando o jogo NÃO é finalizado")
    void shouldLeaveGameSuccessfullyWhenNotGameOver() {
        // Arrange
        Game game = mock(Game.class);
        User user = mock(User.class);
        LeftGameResult result = mock(LeftGameResult.class);

        when(actorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(LeftGameActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(result));

        when(result.user()).thenReturn(userId);
        when(result.game()).thenReturn(game);
        when(result.gameOver()).thenReturn(Optional.empty());

        when(userRepository.find(userId)).thenReturn(Optional.of(user));

        // Act
        LeftGameOutput output = useCase.execute(input);

        // Assert
        assertNotNull(output);
        assertEquals(game, output.game());
        assertTrue(output.gameOver().isEmpty());

        verify(user).leaveGame();
        verify(userRepository).save(user);
        verify(gameRepository).save(game);
        verify(actorManager, never()).remove(any());
    }

    @Test
    @DisplayName("Deve encerrar a partida e remover o ator quando for detectado Game Over")
    void shouldRemoveActorWhenGameOverIsPresent() {
        // Arrange
        Game game = mock(Game.class);
        User user = mock(User.class);
        GameOver gameOver = mock(GameOver.class);
        LeftGameResult result = mock(LeftGameResult.class);

        when(game.getId()).thenReturn(gameId);
        when(actorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(LeftGameActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(result));

        when(result.user()).thenReturn(userId);
        when(result.game()).thenReturn(game);
        when(result.gameOver()).thenReturn(Optional.of(gameOver));

        when(userRepository.find(userId)).thenReturn(Optional.of(user));

        // Act
        LeftGameOutput output = useCase.execute(input);

        // Assert
        assertNotNull(output);
        assertEquals(game, output.game());
        assertTrue(output.gameOver().isPresent());
        assertEquals(gameOver, output.gameOver().get());

        verify(user).leaveGame();
        verify(actorManager).remove(gameId);
        verify(userRepository).save(user);
        verify(gameRepository).save(game);
    }

    @Test
    @DisplayName("Deve lançar exceção e interromper o fluxo quando o usuário não for encontrado")
    void shouldThrowExceptionWhenUserDoesNotExist() {
        // Arrange
        LeftGameResult result = mock(LeftGameResult.class);

        when(actorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(LeftGameActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(result));
        when(result.user()).thenReturn(userId);

        when(userRepository.find(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                UserNotFoundException.class,
                () -> useCase.execute(input)
        );

        verify(userRepository, never()).save(any());
        verify(gameRepository, never()).save(any());
        verify(actorManager, never()).remove(any());
    }

    @Test
    @DisplayName("Deve enviar o comando de saída com a sessão correta para o Actor")
    void shouldSendLeftGameCommandWithCorrectSessionToActor() {
        Game game = mock(Game.class);
        User user = mock(User.class);
        LeftGameResult result = mock(LeftGameResult.class);

        when(actorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any())).thenReturn(CompletableFuture.completedFuture(result));

        when(result.user()).thenReturn(userId);
        when(result.game()).thenReturn(game);
        when(result.gameOver()).thenReturn(Optional.empty());
        when(userRepository.find(userId)).thenReturn(Optional.of(user));

        useCase.execute(input);

        verify(actor).enqueueCommand(refEq(new LeftGameActorCommand(session)));
    }
}