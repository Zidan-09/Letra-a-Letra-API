package com.letraaletra.api.application.game.usecase;

import com.letraaletra.api.application.game.service.GenerateRoomCode;
import com.letraaletra.api.application.game.service.MapParticipantsService;
import com.letraaletra.api.application.game.service.TimeoutManager;
import com.letraaletra.api.infra.service.GlobalTokenService;
import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.game.RoomSettings;
import com.letraaletra.api.domain.participant.ParticipantRole;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;
import com.letraaletra.api.infra.websocket.BroadcastService;
import com.letraaletra.api.presentation.dto.mappers.GameDTOMapper;
import com.letraaletra.api.presentation.dto.response.participant.ParticipantDTO;
import com.letraaletra.api.presentation.dto.response.websocket.GameUpdatedWsResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateGameUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GlobalTokenService globalTokenService;

    @Mock
    private GenerateRoomCode generateRoomCode;

    @Mock
    private TimeoutManager timeoutManager;

    @Mock
    private GameDTOMapper gameDTOMapper;

    @Mock
    private BroadcastService broadCast;

    @Mock
    private MapParticipantsService mapParticipantsService;

    @InjectMocks
    private CreateGameUseCase createGame;

    @Test
    @DisplayName("Should create a game and broadcast the response")
    void execute_success() {
        String userId = "user123";
        String sessionId = "session123";
        String gameName = "My Game";
        RoomSettings gameSettings = new RoomSettings(false, false);
        User user = new User(userId, "nickname", "avatar", "email@email.com", "hash");

        when(userRepository.find(userId)).thenReturn(user);
        when(generateRoomCode.execute()).thenReturn("code");
        when(globalTokenService.generateToken(anyString())).thenReturn("token123");
        when(mapParticipantsService.execute(any(Game.class)))
                .thenReturn(List.of(new ParticipantDTO(userId, "nickname", "avatar", ParticipantRole.PLAYER)));
        when(gameDTOMapper.toDTO(any(Game.class), anyString(), anyList()))
                .thenReturn(Mockito.mock(com.letraaletra.api.presentation.dto.response.game.GameDTO.class));

        createGame.execute(gameName, gameSettings, sessionId, userId);

        verify(userRepository).find(userId);
        verify(gameRepository).save(any(Game.class));
        verify(globalTokenService).generateToken(anyString());
        verify(mapParticipantsService).execute(any(Game.class));
        verify(gameDTOMapper).toDTO(any(Game.class), eq("token123"), anyList());
        verify(broadCast).send(anyString(), any(GameUpdatedWsResponse.class));
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user is null")
    void notCreateBecauseUser() {
        String userId = "user123";
        String sessionId = "session123";
        String gameName = "My Game";
        RoomSettings gameSettings = new RoomSettings(false, false);

        when(userRepository.find(userId)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () ->
                createGame.execute(gameName, gameSettings, sessionId, userId)
        );

        verify(gameRepository, never()).save(any());
        verify(broadCast, never()).send(anyString(), any());
    }
}