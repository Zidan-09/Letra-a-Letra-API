package com.letraaletra.api.features.game.infrastructure.service;

import com.letraaletra.api.features.game.domain.CloseRoomResult;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.RoomCloseReasons;
import com.letraaletra.api.features.game.domain.actor.command.CloseGameActorCommand;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
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

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloseRoomDueToTimeoutServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActorManager<Game> actorManager;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private Actor actor;

    @InjectMocks
    private CloseRoomDueToTimeoutService service;

    private UUID gameId;
    private Game game;

    @BeforeEach
    void setup() {
        gameId = UUID.randomUUID();
        game = mock(Game.class);
        lenient().when(game.getId()).thenReturn(gameId);
    }

    @Test
    @DisplayName("Deve fechar a sala por inatividade com sucesso")
    void shouldCloseRoomSuccessfullyDueToTimeout() {
        // Arrange
        when(actorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(CloseGameActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(game));

        // Act
        CloseRoomResult output = service.close(game);

        // Assert
        assertNotNull(output);
        assertEquals(game, output.game());
        assertEquals("ROOM_CLOSED", output.event());
        assertEquals(RoomCloseReasons.INACTIVITY, output.reason());

        // Verificações das interações do Serviço
        verify(actorManager).get(gameId);
        verify(actor).enqueueCommand(any(CloseGameActorCommand.class));
        verify(actorManager).remove(gameId);
        verify(gameRepository).save(game);
    }

    @Test
    @DisplayName("Lança exceção quando o processamento do comando do Actor falha no join()")
    void shouldThrowExceptionWhenActorCommandFails() {
        // Arrange
        CompletableFuture<Game> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Erro no processamento do actor"));

        when(actorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(CloseGameActorCommand.class))).thenReturn(failedFuture);

        // Act & Assert
        // Chamadas com .join() lançam CompletionException quando o Future falha
        assertThrows(CompletionException.class, () -> service.close(game));

        // Se o actor falhar no .join(), não deve remover nem salvar o estado inconsistente
        verify(actorManager, never()).remove(any());
        verify(gameRepository, never()).save(any());
    }
}