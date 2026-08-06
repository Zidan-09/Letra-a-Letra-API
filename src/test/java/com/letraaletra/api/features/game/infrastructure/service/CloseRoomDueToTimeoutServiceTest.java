package com.letraaletra.api.features.game.infrastructure.service;

import com.letraaletra.api.features.game.domain.CloseRoomResult;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.RoomCloseReasons;
import com.letraaletra.api.features.game.domain.actor.command.CloseGameActorCommand;
import com.letraaletra.api.features.game.domain.participants.Participants;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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
    private Participants participants;

    @BeforeEach
    void setup() {
        gameId = UUID.randomUUID();
        game = mock(Game.class);
        participants = mock(Participants.class);

        lenient().when(game.getId()).thenReturn(gameId);
        lenient().when(game.getParticipants()).thenReturn(participants);
    }

    @Test
    @DisplayName("Deve fechar a sala por inatividade e atualizar os usuários com sucesso")
    void shouldCloseRoomSuccessfullyDueToTimeout() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);
        List<UUID> participantIds = List.of(userId);
        List<User> userList = List.of(user);

        when(participants.getIds()).thenReturn(participantIds);
        when(userRepository.findUsersById(participantIds)).thenReturn(userList);

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

        // Verificações
        verify(actorManager).get(gameId);
        verify(actor).enqueueCommand(any(CloseGameActorCommand.class));
        verify(userRepository).findUsersById(participantIds);
        verify(user).leaveGame();
        verify(userRepository).saveAll(userList);
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
        assertThrows(CompletionException.class, () -> service.close(game));

        // Verificações de segurança no caso de falha
        verify(userRepository, never()).findUsersById(any());
        verify(userRepository, never()).saveAll(any());
        verify(actorManager, never()).remove(any());
        verify(gameRepository, never()).save(any());
    }
}