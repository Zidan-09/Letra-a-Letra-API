package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.application.port.DisconnectScheduler;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.participant.application.input.ReconnectParticipantInput;
import com.letraaletra.api.features.participant.application.output.ReconnectParticipantOutput;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReconnectUseCaseTest {

    @Mock private ActorManager<Game> actorManager;
    @Mock private DisconnectScheduler disconnectScheduler;
    @Mock private UserRepository userRepository;
    @Mock private Actor actor;
    @Mock private Game mockGame;
    @Mock private Participant mockParticipant;
    @Mock private User mockUser;

    @InjectMocks
    private ReconnectUseCase reconnectUseCase;

    @Test
    @DisplayName("Deve reconectar com sucesso, cancelando o agendamento de queda")
    void shouldReconnectParticipantSuccessfully() {
        String userId = "user-789";
        String gameId = "game-123";
        String wsSessionId = "session-new";
        ReconnectParticipantInput input = new ReconnectParticipantInput(userId, wsSessionId);

        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotInGame()).thenReturn(false);
        when(mockUser.getCurrentGameId()).thenReturn(gameId);

        when(actorManager.get(gameId)).thenReturn(actor);
        when(actor.getGame()).thenReturn(mockGame);
        when(mockGame.getId()).thenReturn(gameId);
        when(mockGame.getParticipantByUserId(userId)).thenReturn(mockParticipant);

        Optional<ReconnectParticipantOutput> result = reconnectUseCase.execute(input);

        assertTrue(result.isPresent());
        verify(disconnectScheduler, times(1)).cancel(userId, gameId);
        verify(mockGame, times(1)).reconnect(userId, wsSessionId);
    }

    @Test
    @DisplayName("Deve capturar qualquer exceção na reconexão, ejetar o usuário do jogo e salvar")
    void shouldFallbackAndRemoveUserFromGameOnException() {
        String userId = "user-789";
        String gameId = "game-123";
        ReconnectParticipantInput input = new ReconnectParticipantInput(userId, "session-any");

        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotInGame()).thenReturn(false);
        when(mockUser.getCurrentGameId()).thenReturn(gameId);

        // Simula erro ao buscar o Ator (sala expirou ou deu erro de concorrência)
        when(actorManager.get(gameId)).thenThrow(new RuntimeException("Actor thread error"));

        Optional<ReconnectParticipantOutput> result = reconnectUseCase.execute(input);

        assertTrue(result.isEmpty(), "Deveria retornar Optional.empty() por conta do catch");
        verify(mockUser, times(1)).leaveGame();
        verify(userRepository, times(1)).save(mockUser);
    }
}