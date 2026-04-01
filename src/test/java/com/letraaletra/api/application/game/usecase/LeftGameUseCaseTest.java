package com.letraaletra.api.application.game.usecase;

import com.letraaletra.api.application.game.service.MapParticipantsService;
import com.letraaletra.api.application.usecase.game.LeftGameUseCase;
import com.letraaletra.api.domain.participant.ParticipantRole;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.infrastructure.security.JsonWebTokenService;
import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.game.exceptions.GameNotFoundException;
import com.letraaletra.api.domain.game.exceptions.UserNotInGameException;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.infrastructure.websocket.BroadcastService;
import com.letraaletra.api.presentation.mappers.game.GameDTOMapper;
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
    private JsonWebTokenService jsonWebTokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MapParticipantsService mapParticipantsService;

    @Mock
    private GameDTOMapper gameDTOMapper;

    @Mock
    private BroadcastService broadcastService;

    @InjectMocks
    private LeftGameUseCase leftGameUseCase;

    private final String tokenGameId = "token123";
    private final String gameId = "game123";
    private final String sessionId = "session1";

    @Mock
    private Game game;

    @BeforeEach
    void setup() {
        when(jsonWebTokenService.getTokenContent(tokenGameId)).thenReturn(gameId);
    }

    @Test
    @DisplayName("Should remove participant from game successfully")
    void execute_success() {
        when(gameRepository.find(gameId)).thenReturn(game);
        String userId = "user1";
        Participant participant = new Participant(userId, "sid", "test", "avatar", ParticipantRole.PLAYER);
        when(game.getParticipant(sessionId)).thenReturn(participant);
        User user = new User(userId, "test", "avatar", "test@email.com", "hash");
        when(userRepository.find(userId)).thenReturn(user);

        leftGameUseCase.execute(tokenGameId, sessionId);

        verify(game).remove(userId);
        verify(mapParticipantsService).execute(game);
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