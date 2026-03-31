package com.letraaletra.api.application.game.usecase;

import com.letraaletra.api.application.game.service.MapParticipantsService;
import com.letraaletra.api.infra.service.GlobalTokenService;
import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.game.exceptions.GameIsRunningException;
import com.letraaletra.api.domain.game.exceptions.GameNotFoundException;
import com.letraaletra.api.domain.game.exceptions.UserNotInGameException;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.infra.websocket.BroadcastService;
import com.letraaletra.api.presentation.dto.mappers.GameDTOMapper;
import com.letraaletra.api.presentation.dto.response.game.GameDTO;
import com.letraaletra.api.presentation.dto.response.participant.ParticipantDTO;
import com.letraaletra.api.presentation.dto.response.websocket.GameUpdatedWsResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SwapRoomPositionUseCaseTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GlobalTokenService globalTokenService;

    @Mock
    private GameDTOMapper gameDTOMapper;

    @Mock
    private MapParticipantsService mapParticipantsService;

    @Mock
    private BroadcastService broadcast;

    @InjectMocks
    private SwapRoomPositionUseCase swapRoomPosition;

    private Game game;
    private Participant participant;

    private final String tokenGameId = "token-123";
    private final String gameId = "game-123";
    private final String userId = "user-123";
    private final int newPosition = 2;

    @BeforeEach
    void setup() {
        game = mock(Game.class);
        participant = mock(Participant.class);

        lenient().when(game.getPositions()).thenReturn(Map.of());
    }

    @Test
    void shouldSwapPositionSuccessfully() {
        List<ParticipantDTO> participantsDTO = List.of(mock(ParticipantDTO.class));
        GameDTO gameDTO = new GameDTO("tokenId", "test", List.of(), game.getPositions());

        when(globalTokenService.getTokenContent(tokenGameId)).thenReturn(gameId);
        when(gameRepository.find(gameId)).thenReturn(game);
        when(game.getGameStatus()).thenReturn(GameStatus.WAITING);
        when(game.getParticipantByUserId(userId)).thenReturn(participant);
        when(mapParticipantsService.execute(game)).thenReturn(participantsDTO);
        when(gameDTOMapper.toDTO(game, tokenGameId, participantsDTO)).thenReturn(gameDTO);

        swapRoomPosition.execute(tokenGameId, newPosition, userId);

        verify(game).changePosition(userId, newPosition);
        verify(gameRepository).save(game);
        verify(mapParticipantsService).execute(game);
        verify(gameDTOMapper).toDTO(game, tokenGameId, participantsDTO);
        verify(broadcast).send(eq(gameId), any(GameUpdatedWsResponse.class));
    }

    @Test
    void shouldThrowWhenGameNotFound() {
        when(globalTokenService.getTokenContent(tokenGameId)).thenReturn(gameId);
        when(gameRepository.find(gameId)).thenReturn(null);

        assertThrows(GameNotFoundException.class,
                () -> swapRoomPosition.execute(tokenGameId, newPosition, userId));

        verify(gameRepository, never()).save(any());
        verify(broadcast, never()).send(any(), any());
    }

    @Test
    void shouldThrowWhenGameIsRunning() {
        when(globalTokenService.getTokenContent(tokenGameId)).thenReturn(gameId);
        when(gameRepository.find(gameId)).thenReturn(game);
        when(game.getGameStatus()).thenReturn(GameStatus.RUNNING);

        assertThrows(GameIsRunningException.class,
                () -> swapRoomPosition.execute(tokenGameId, newPosition, userId));

        verify(gameRepository, never()).save(any());
        verify(broadcast, never()).send(any(), any());
    }

    @Test
    void shouldThrowWhenUserNotInGame() {
        when(globalTokenService.getTokenContent(tokenGameId)).thenReturn(gameId);
        when(gameRepository.find(gameId)).thenReturn(game);
        when(game.getGameStatus()).thenReturn(GameStatus.WAITING);
        when(game.getParticipantByUserId(userId)).thenReturn(null);

        assertThrows(UserNotInGameException.class,
                () -> swapRoomPosition.execute(tokenGameId, newPosition, userId));

        verify(gameRepository, never()).save(any());
        verify(broadcast, never()).send(any(), any());
    }

    @Test
    void shouldExecuteInCorrectOrder() {
        List<ParticipantDTO> participantsDTO = List.of(mock(ParticipantDTO.class));
        GameDTO gameDTO = new GameDTO("tokenId", "test", List.of(), game.getPositions());

        when(globalTokenService.getTokenContent(tokenGameId)).thenReturn(gameId);
        when(gameRepository.find(gameId)).thenReturn(game);
        when(game.getGameStatus()).thenReturn(GameStatus.WAITING);
        when(game.getParticipantByUserId(userId)).thenReturn(participant);
        when(mapParticipantsService.execute(game)).thenReturn(participantsDTO);
        when(gameDTOMapper.toDTO(game, tokenGameId, participantsDTO)).thenReturn(gameDTO);

        swapRoomPosition.execute(tokenGameId, newPosition, userId);

        var inOrder = inOrder(game, gameRepository, mapParticipantsService, gameDTOMapper, broadcast);

        inOrder.verify(game).changePosition(userId, newPosition);
        inOrder.verify(gameRepository).save(game);
        inOrder.verify(mapParticipantsService).execute(game);
        inOrder.verify(gameDTOMapper).toDTO(game, tokenGameId, participantsDTO);
        inOrder.verify(broadcast).send(eq(gameId), any(GameUpdatedWsResponse.class));
    }
}