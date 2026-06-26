package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.actor.command.DisconnectParticipantActorCommand;
import com.letraaletra.api.features.matchmaking.domain.repository.MatchmakingRepository;
import com.letraaletra.api.features.participant.application.input.DisconnectParticipantInput;
import com.letraaletra.api.features.participant.application.output.DisconnectParticipantOutput;
import com.letraaletra.api.features.user.domain.User;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisconnectUseCaseTest {

    @Mock private ActorManager<Game> gameActorManager;
    @Mock private MatchmakingRepository matchmakingRepository;
    @Mock private UserRepository userRepository;
    @Mock private Actor actor;
    @Mock private Game mockGame;
    @Mock private User mockUser;

    @InjectMocks
    private DisconnectUseCase disconnectUseCase;

    private UUID userId;
    private UUID gameId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        gameId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Deve remover da fila se o usuário desconectado estiver buscando partida")
    void shouldRemoveFromMatchmakingIfUserIsOnQueue() {
        DisconnectParticipantInput input = new DisconnectParticipantInput(userId, "session-xyz");

        when(matchmakingRepository.onQueue(userId)).thenReturn(true);
        when(userRepository.find(userId)).thenReturn(Optional.empty());

        Optional<DisconnectParticipantOutput> result = disconnectUseCase.execute(input);

        assertTrue(result.isEmpty());
        verify(matchmakingRepository, times(1)).remove(userId);
    }

    @Test
    @DisplayName("Deve desconectar com sucesso do Ator e manter usuário se a sala persistir")
    void shouldDisconnectSuccessfullyWhenGamePersists() {
        DisconnectParticipantInput input = new DisconnectParticipantInput(userId, "session-xyz");

        when(matchmakingRepository.onQueue(userId)).thenReturn(false);
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotInGame()).thenReturn(false);
        when(mockUser.getCurrentGameId()).thenReturn(gameId);

        when(gameActorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(DisconnectParticipantActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(mockGame)));

        Optional<DisconnectParticipantOutput> result = disconnectUseCase.execute(input);

        assertTrue(result.isPresent());
        assertEquals(mockGame, result.get().game());
        assertEquals(userId, result.get().user());
        verify(mockUser, never()).leaveGame();
    }

    @Test
    @DisplayName("Deve forçar o usuário a sair da partida se o Ator retornar um jogo vazio")
    void shouldForceUserToLeaveGameWhenActorReturnsEmptyGame() {
        DisconnectParticipantInput input = new DisconnectParticipantInput(userId, "session-xyz");

        when(matchmakingRepository.onQueue(userId)).thenReturn(false);
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotInGame()).thenReturn(false);
        when(mockUser.getCurrentGameId()).thenReturn(gameId);

        when(gameActorManager.get(gameId)).thenReturn(actor);
        when(actor.enqueueCommand(any(DisconnectParticipantActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        Optional<DisconnectParticipantOutput> result = disconnectUseCase.execute(input);

        assertTrue(result.isEmpty());
        verify(mockUser, times(1)).leaveGame();
        verify(userRepository, times(1)).save(mockUser);
    }
}