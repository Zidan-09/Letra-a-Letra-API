package com.letraaletra.api.application.game.usecase;

import com.letraaletra.api.application.game.service.MapParticipantsService;
import com.letraaletra.api.application.user.service.TokenService;
import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.game.exceptions.GameNotFoundException;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.participant.ParticipantRole;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.SessionRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;
import com.letraaletra.api.infra.websocket.BroadcastService;
import com.letraaletra.api.presentation.dto.mappers.GameDTOMapper;
import com.letraaletra.api.presentation.dto.response.game.GameDTO;
import com.letraaletra.api.presentation.dto.response.participant.ParticipantDTO;
import com.letraaletra.api.presentation.dto.response.websocket.GameUpdatedWsResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JoinGameUseCaseTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MapParticipantsService mapParticipantsService;

    @Mock
    private GameDTOMapper gameDTOMapper;

    @Mock
    private BroadcastService broadCast;

    @InjectMocks
    private JoinGameUseCase joinGameUseCase;

    private Game game;
    private final String tokenGameId = "token123";
    private final String gameId = "game123";
    private final String sessionId = "session123";
    private final String userId = "user123";

    @BeforeEach
    void setup() {
        game = mock(Game.class);
        WebSocketSession session = mock(WebSocketSession.class);
        User user = new User(userId, "nick", "avatar", "email@email.com", "hash");

        lenient().when(tokenService.getTokenContent(tokenGameId)).thenReturn(gameId);
        lenient().when(gameRepository.find(gameId)).thenReturn(game);
        lenient().when(sessionRepository.find(sessionId)).thenReturn(session);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("userId", userId);
        lenient().when(session.getAttributes()).thenReturn(attributes);
        lenient().when(userRepository.find(userId)).thenReturn(user);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException if user is null")
    void execute_userNotFound() {
        when(userRepository.find(userId)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () ->
                joinGameUseCase.execute(tokenGameId, sessionId, userId)
        );
    }

    @Test
    @DisplayName("Should throw GameNotFoundException if game is null")
    void execute_gameNotFound() {
        when(gameRepository.find(gameId))
                .thenReturn(null);

        Assertions.assertThrows(GameNotFoundException.class, () -> joinGameUseCase.execute(tokenGameId, sessionId, userId));
    }

    @Test
    @DisplayName("Should join game successfully and broadcast")
    void execute_success() {
        ParticipantDTO participantDTO = new ParticipantDTO(userId, "nick", "avatar", ParticipantRole.PLAYER);
        List<ParticipantDTO> participants = List.of(participantDTO);

        GameDTO expectedGameDTO = new GameDTO(tokenGameId, "name", participants);

        when(game.nextParticipantRole()).thenReturn(ParticipantRole.PLAYER);
        when(mapParticipantsService.execute(game)).thenReturn(participants);
        when(gameDTOMapper.toDTO(any(), anyString(), anyList())).thenReturn(expectedGameDTO);

        joinGameUseCase.execute(tokenGameId, sessionId, userId);

        verify(game).join(any(Participant.class));

        verify(mapParticipantsService).execute(game);

        verify(gameDTOMapper).toDTO(eq(game), eq(tokenGameId), eq(participants));
        verify(broadCast).send(eq("game123"), any(GameUpdatedWsResponse.class));
    }
}