package com.letraaletra.api.application.game.usecase;

import com.letraaletra.api.application.user.service.TokenService;
import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.participant.ParticipantRole;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.game.exceptions.GameNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindByTokenGameIdUseCaseTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private FindByTokenGameIdUseCase findByTokenGameIdUseCase;

    @Test
    @DisplayName("Should return a game given a valid tokenGameId")
    void execute_success() {
        String tokenGameId = "token123";
        String gameId = "game123";
        Participant participant = new Participant("id", "sId", "test", "avatar1", ParticipantRole.PLAYER);

        Game game = new Game(gameId, "My Game", null, participant);

        when(tokenService.getTokenContent(tokenGameId)).thenReturn(gameId);
        when(gameRepository.find(gameId)).thenReturn(game);

        Game result = findByTokenGameIdUseCase.execute(tokenGameId);

        assertNotNull(result);
        assertEquals(gameId, result.getId());

        verify(tokenService).getTokenContent(tokenGameId);
        verify(gameRepository).find(gameId);
    }

    @Test
    @DisplayName("Should throw GameNotFoundException when game does not exist")
    void execute_gameNotFound() {
        String tokenGameId = "token123";
        String gameId = "game123";

        when(tokenService.getTokenContent(tokenGameId)).thenReturn(gameId);
        when(gameRepository.find(gameId)).thenReturn(null);

        assertThrows(GameNotFoundException.class, () ->
                findByTokenGameIdUseCase.execute(tokenGameId)
        );

        verify(tokenService).getTokenContent(tokenGameId);
        verify(gameRepository).find(gameId);
    }
}