package com.letraaletra.api.application.game.usecase;

import com.letraaletra.api.infra.service.GlobalTokenService;
import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.game.exceptions.GameNotFoundException;
import com.letraaletra.api.domain.game.exceptions.UserNotInGameException;
import com.letraaletra.api.domain.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeftGameUseCaseTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GlobalTokenService globalTokenService;

    @InjectMocks
    private LeftGameUseCase leftGameUseCase;

    private final String tokenGameId = "token123";
    private final String gameId = "game123";
    private final String sessionId = "session1";

    @Mock
    private Game game;

    @Mock
    private Participant participant;

    @BeforeEach
    void setup() {
        when(globalTokenService.getTokenContent(tokenGameId)).thenReturn(gameId);
    }

    @Test
    @DisplayName("Should remove participant from game successfully")
    void execute_success() {
        when(gameRepository.find(gameId)).thenReturn(game);
        when(game.getParticipant(sessionId)).thenReturn(participant);
        String userId = "user1";
        when(participant.getUserId()).thenReturn(userId);

        leftGameUseCase.execute(tokenGameId, sessionId);

        verify(game).remove(userId);
    }

    @Test
    @DisplayName("Should throw GameNotFoundException when game does not exist")
    void execute_gameNotFound() {
        when(gameRepository.find(gameId)).thenReturn(null);

        assertThrows(GameNotFoundException.class, () ->
                leftGameUseCase.execute(tokenGameId, sessionId)
        );
    }

    @Test
    @DisplayName("Should throw UserNotInGameException when participant not found in game")
    void execute_participantNotFound() {
        when(gameRepository.find(gameId)).thenReturn(game);
        when(game.getParticipant(sessionId)).thenReturn(null);

        assertThrows(UserNotInGameException.class, () ->
                leftGameUseCase.execute(tokenGameId, sessionId)
        );
    }
}