package com.letraaletra.api.application.game.usecase;

import com.letraaletra.api.application.game.service.MapParticipantsService;
import com.letraaletra.api.application.user.service.TokenService;
import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.game.GameSettings;
import com.letraaletra.api.domain.game.RoomSettings;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.participant.ParticipantRole;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.presentation.dto.mappers.GameDTOMapper;
import com.letraaletra.api.presentation.dto.response.game.GameDTO;
import com.letraaletra.api.presentation.dto.response.participant.ParticipantDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPublicGamesUseCaseTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private GameDTOMapper gameDTOMapper;

    @Mock
    private MapParticipantsService mapParticipants;

    @InjectMocks
    private GetPublicGamesUseCase getPublicGames;

    @Test
    @DisplayName("Should return a List of games with public game setting true")
    void execute() {
        RoomSettings settingsPublic = new RoomSettings(false, true);
        RoomSettings settingsPrivate = new RoomSettings(false, false);

        Participant host = new Participant("user1", "session1", "test", "avatar1", ParticipantRole.PLAYER);

        Game publicGame = new Game("id1", "test1", settingsPublic, host);
        Game privateGame = new Game("id2", "test2", settingsPrivate, host);

        List<Game> games = List.of(publicGame, privateGame);

        when(gameRepository.get()).thenReturn(games);

        when(tokenService.generateToken(anyString()))
                .thenReturn("token-game-id-fake");

        when(mapParticipants.execute(any(Game.class)))
                .thenReturn(List.of(
                        new ParticipantDTO("user1", "nick", "avatar", ParticipantRole.PLAYER)
                ));

        GameDTO gameDTO = mock(GameDTO.class);

        when(gameDTOMapper.toDTO(any(Game.class), anyString(), anyList()))
                .thenReturn(gameDTO);

        List<GameDTO> gameDTOS = getPublicGames.execute();

        assertNotNull(gameDTOS);
        assertEquals(1, gameDTOS.size());

        verify(gameRepository).get();
        verify(tokenService, times(1)).generateToken(anyString());
        verify(mapParticipants).execute(any(Game.class));
        verify(gameDTOMapper).toDTO(any(Game.class), anyString(), anyList());
    }
}